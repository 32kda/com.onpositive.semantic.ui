package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AttributesHandler {

	
	private final HashMap<String, ArrayList<Object>> aHandlers = new HashMap<String, ArrayList<Object>>();
	

	protected void addAttributeHandler(String string, String h) {
		ArrayList<Object> arrayList = aHandlers.get(string);
		if (arrayList == null) {
			arrayList = new ArrayList<Object>();
			aHandlers.put(string, arrayList);
		}
		arrayList.add(h);
	}
	
	protected void addAttributeHandler(String string, AttributeHandler<?> h) {
		ArrayList<Object> arrayList = aHandlers.get(string);
		if (arrayList == null) {
			arrayList = new ArrayList<Object>();
			aHandlers.put(string, arrayList);
		}
		arrayList.add(h);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addAttributeHandlers(Class<?>clazz){
		Method[] methods = clazz.getMethods();
		for (Method m:methods){
			HandlesAttributeDirectly annotation = m.getAnnotation(HandlesAttributeDirectly.class);
			if(annotation!=null){
				Class<?>[] parameterTypes = m.getParameterTypes();
				if (parameterTypes.length!=1){
					throw new IllegalStateException(m.getName()+":"+clazz.getName());
				}
				if (parameterTypes[0]==String.class){
					addAttributeHandler(annotation.value(), new MethodAttributeHandler(clazz, m));
				}
				else if (parameterTypes[0]==Boolean.class||parameterTypes[0]==boolean.class){
					addAttributeHandler(annotation.value(), new MethodAttributeHandler(clazz, m){
						@Override
						protected Object convert(String value) {
							return Boolean.parseBoolean(value);
						}
						
					});
				}
				else{
					addAttributeHandler(annotation.value(), new MethodAttributeHandler(clazz, m){
						
						@Override
						public
						void handle(Object element, Object pContext, String value, Context ctx) {
							try {
								method.invoke(element, ctx.newInstance(value));
							} catch (Exception e) {
								throw new IllegalStateException(value);
							}
						}
						
					});
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleAttrs(Element element, Object parentContext, Context ctx, final Object newInstance)
	{
		final NamedNodeMap attributes = element.getAttributes();
		final int length2 = attributes.getLength();
		for (int a = 0; a < length2; a++) {
			final Node item = attributes.item(a);
			final ArrayList<Object> attributeHandlers = (ArrayList<Object>) this.aHandlers
					.get(item.getLocalName());
			if (attributeHandlers==null){
				//System.out.println("No attribute handler for :"+item.getLocalName());
				continue;
			}
			ArrayList<AttributeHandler> rH = new ArrayList<AttributeHandler>();
			for (Object o : attributeHandlers) {
				if (o instanceof String) {
					try {
						Class<?> forName = Class.forName(o.toString());
						rH.add((AttributeHandler) forName.newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (o instanceof Class) {
					Class z = (Class) o;
					try {
						rH.add((AttributeHandler) z.newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (o instanceof AttributeHandler){
					rH.add((AttributeHandler) o);
				}
			}
			if (attributeHandlers != null) {
				for (AttributeHandler<? extends Object> attributeHandler : rH) {
					AttributeHandler attributeHandler2 = (AttributeHandler) attributeHandler;
					if (attributeHandler2.isApplyable(newInstance)) {
						if (attributeHandler != null) {
							attributeHandler2.handle(newInstance,
									parentContext, item.getNodeValue(), ctx);
							break;
						}
					}
				}
			}
		}
	}

}
