package com.onpositive.semantic.model.api.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;

public class SimpleXMLAccess {

	public static String write(Object obj, boolean indent) {
		StringWriter w = new StringWriter();
		write(obj, w, indent);
		try {
			w.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return w.toString();
	}

	public static <T> T read(InputStream r, Class<T> cls) {
		try {
			try {
				Document parse = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(r);
				return cls.cast(read(parse.getDocumentElement(), cls));
			} catch (SAXParseException e) {
				return null;
			}

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> T read(Reader r, Class<T> cls) {
		try {
			try {
				Document parse = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(new InputSource(r));
				return cls.cast(read(parse.getDocumentElement(), cls));
			} catch (SAXParseException e) {
				return null;
			}

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object read(Element documentElement, Class<?> cls) {
		try {
			Constructor<?> declaredConstructor = cls.getDeclaredConstructor();
			declaredConstructor.setAccessible(true);
			Object newInstance = declaredConstructor.newInstance();
			Iterable<IProperty> properties = PropertyAccess
					.getProperties(newInstance);
			HashMap<String, IProperty> qm = new HashMap<String, IProperty>();
			CompositeCommand cm=new CompositeCommand();
			for (IProperty q : properties) {
				if (!DefaultMetaKeys.isPersistent(q)){
					continue;
				}
				if (DefaultMetaKeys.isComputed(q)
						|| DefaultMetaKeys.isStatic(q)
						|| DefaultMetaKeys.getValue(q,
								DefaultMetaKeys.PUBLIC_KEY)) {
					continue;
				}
				String name2 = DefaultMetaKeys.getName(q);
				if (name2 == null) {
					name2 = q.getId();
				}
				qm.put(name2, q);
				String attribute = documentElement.getAttribute(name2);
				Object value=attribute;
				if (attribute.length() > 0) {
					Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(q);
					if (subjectClass!=String.class){
						if (subjectClass==Integer.class){
							value=Integer.parseInt(attribute);
						}
						else if (subjectClass==Double.class){
							value=Double.parseDouble(attribute);
						}
						else if (subjectClass==Long.class){
							value=Long.parseLong(attribute);
						}
						else if (subjectClass==Short.class){
							value=Short.parseShort(attribute);
						}
						else if (subjectClass==Float.class){
							value=Float.parseFloat(attribute);
						}
						else if (subjectClass==Character.class){
							value=attribute.charAt(0);
						}
						else if (subjectClass==Boolean.class){
							value=Boolean.parseBoolean(attribute);
						}
						//TODO FIX ME
					}
					cm.addCommand(PropertyAccess.createSetValueCommand(newInstance, value,q));
				}
			}
			NodeList childNodes = documentElement.getChildNodes();
			for (int a = 0; a < childNodes.getLength(); a++) {
				Node item = childNodes.item(a);
				if (item instanceof Element) {
					IProperty iProperty = qm.get(item.getNodeName());
					if (iProperty != null) {
						cm.addCommand(PropertyAccess.createAddValueCommand(
								newInstance,
								read((Element)item, DefaultMetaKeys
										.getSubjectClass(iProperty)),iProperty));
					}
				}
			}
			cm.setSilent();
			cm.execute();
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void write(Object object, Writer writer, boolean indent) {
		try {
			Document newDocument = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			writeObject(object, newDocument,null);
			Transformer newTransformer = TransformerFactory.newInstance()
					.newTransformer();
			if (indent) {
				newTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
				newTransformer.setOutputProperty(
						OutputKeys.OMIT_XML_DECLARATION, "yes");
				newTransformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");
			}
			newTransformer.transform(new DOMSource(newDocument),
					new StreamResult(writer));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void writeObject(Object object, Node parentNode,String name) {
		Iterable<IProperty> properties = PropertyAccess.getProperties(object);
		IHasMeta meta = MetaAccess.getMeta(object);
		
		if (name == null) {
			name = DefaultMetaKeys.getName(meta);
			if (name==null){
				name = object.getClass().getSimpleName().toLowerCase();
			}
		}
		Document ownerDocument = parentNode instanceof Document ? (Document) parentNode
				: parentNode.getOwnerDocument();
		Element createElement = ownerDocument.createElement(name);
		parentNode.appendChild(createElement);
		for (IProperty q : properties) {
			if (!DefaultMetaKeys.isPersistent(q)){
				continue;
			}
			if (DefaultMetaKeys.isComputed(q) || DefaultMetaKeys.isStatic(q)
					|| DefaultMetaKeys.getValue(q, DefaultMetaKeys.PUBLIC_KEY)|| DefaultMetaKeys.getValue(q, DefaultMetaKeys.PARENT_KEY)) {
				continue;
			}
			Object value = q.getValue(object);
			String name2 = DefaultMetaKeys.getName(q);
			if (name2==null){
				name2=q.getId();
			}
			if (value != null) {
				if (asAttrubute(value)) {
					if (skip(value)) {
						continue;
					}

					
					createElement.setAttribute(name2, toString(value));
					continue;
				}
			}
			Collection<Object> collection = ValueUtils.toCollection(value);			
			for (Object z : collection) {
				writeObject(z, createElement,name2);
			}
		}
	}

	private static boolean skip(Object value) {
		return vs.contains(value);
	}

	private static String toString(Object value) {
		return value.toString();
	}

	static HashSet<Object> vs = new HashSet<Object>();

	static HashSet<Class<?>> ms = new HashSet<Class<?>>();
	static {
		ms.add(Boolean.class);
		ms.add(Integer.class);
		ms.add(Class.class);
		ms.add(String.class);
		ms.add(Long.class);
		ms.add(Short.class);
		ms.add(Double.class);
		ms.add(Float.class);
		ms.add(Character.class);
		ms.add(int.class);
		ms.add(boolean.class);
		ms.add(double.class);
		ms.add(float.class);
		ms.add(char.class);
		ms.add(short.class);
		ms.add(double.class);
		ms.add(long.class);
		vs.add(false);
		vs.add(0);
		vs.add(0L);
		vs.add(0f);
		vs.add(0.0);
		vs.add((char) 0);
		vs.add((short) 0);
	}

	private static boolean asAttrubute(Object value) {
		return ms.contains(value.getClass());
	}

	public static <T> T read(String toTest, Class<T> cls) {
		try {
			return cls.cast(read(
					new ByteArrayInputStream(toTest.getBytes("UTF-8")), cls));
		} catch (UnsupportedEncodingException e) {

		}
		return null;
	}
}
