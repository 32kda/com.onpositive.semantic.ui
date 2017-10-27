package com.onpositive.commons.xml.language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.platform.registry.NoCacheServiceObject;
import com.onpositive.commons.platform.registry.ServiceMap;
import com.onpositive.commons.platform.registry.ServiceObject;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;

public class DOMEvaluator {

	private final HashMap<String, ObjectReference> attributeMap = new HashMap<String, ObjectReference>();

//	private static final class CloningHandler extends ObjectReference implements
//			IElementHandler {
//		private CloningHandler(Bundle bundleContext, String className) {
//			super(bundleContext, className.trim());
//		}
//
//		@Override
//		public Object getObject() {
//			return this;
//		}
//
//		public Object handleElement(Element element, Object parentContext,
//				Context context) {
//			Object newInstance = newInstance();
//			evaluateChildren(element, newInstance, context);
//			return newInstance;
//		}
//
//		public Method getChildSetter(String childName) {
//			return null;
//		}
//	}

	class NameSpace {

		private final HashMap<String, ElementDefinition> elementMap = new HashMap<String, ElementDefinition>();
		private IConfigurationElement element;
		private String url;
		private boolean loaded;

		public NameSpace(IConfigurationElement e, String attribute) {
			this.element = e;
			this.url = attribute;
		}

		public void load() {
			final String attribute = element.getAttribute("partDefinitionFile");
			if (attribute != null) {
				final String name = element.getContributorId();
				final Bundle bundle = Platform.getBundle(name);

				final InputStream resource = bundle
						.getResourceAsStream(attribute);

				if (resource != null) {
					try {
						final InputStream openStream = resource;
						final Document document = DOMEvaluator.this.builder
								.parse(openStream);
						final Element documentElement = document
								.getDocumentElement();

						final NodeList childNodes = documentElement
								.getChildNodes();
						HashMap<String, ObjectReference> lm = loadImplementation();

						final int childnodesCount = childNodes.getLength();
						for (int i = 0; i < childnodesCount; i++) {
							final Node childNode = childNodes.item(i);

							if (childNode instanceof Element) {
								final Element childElement = (Element) childNode;
								final String elname = childElement
										.getLocalName();
								String childName = childElement
										.getAttribute("name");
								if (elname.equals("element")) {

									ObjectReference objectReference = lm
											.get(childName);
									String isAbstractAttributeValue = childElement
											.getAttribute("isAbstract");
									boolean isAbstract = Boolean
											.parseBoolean(isAbstractAttributeValue);

									if (objectReference == null) {

										if (!isAbstract) {
											Platform.log("Element: "
													+ childName
													+ " from namespace "
													+ url
													+ " is not handled properly");
											continue;
										}
									}
									NodeList childNodes2 = childElement
											.getChildNodes();
									String modelClassAttributeValue = childElement
											.getAttribute("modelClass").trim();
									if (modelClassAttributeValue.length() == 0
											&& !isAbstract) {
										// Platform.log("Model class for element:"
										// + attribute2
										// + " from namespace " + url
										// + " is not defined properly");
									}
									ElementDefinition ds = new ElementDefinition(
											childName, isAbstract,
											objectReference,
											modelClassAttributeValue, url);

									ds.extendedElementsString = childElement
											.getAttribute("extends");
									ds.allowedChildren = childElement
											.getAttribute("allowsChilds");

									for (int j = 0; j < childNodes2.getLength(); j++) {
										Node childNode2 = childNodes2.item(j);
										if (childNode2 instanceof Element) {
											Element childElement2 = (Element) childNode2;

											if (childElement2.getNodeName()
													.equals("property")) {
												String nameAttr = childElement2
														.getAttribute("name");
												String typeAttr = childElement2
														.getAttribute("type");
												String requiredAttr = childElement2
														.getAttribute("required");
												String ignoreOnValidation = childElement2
														.getAttribute("ignoreOnValidation");
												ds.supportedAttrs
														.add(ignoreOnValidation
																.equals("") ? new AttributeDefinition(
																nameAttr,
																typeAttr,
																Boolean.parseBoolean(requiredAttr))
																: new AttributeDefinition(
																		nameAttr,
																		typeAttr,
																		Boolean.parseBoolean(requiredAttr),
																		Boolean.parseBoolean(ignoreOnValidation))

														);
											}
										}
									}

									if (objectReference != null)
										if (objectReference.getObject() instanceof GeneralElementHandler)
											((GeneralElementHandler) objectReference
													.getObject())
													.setElementDefiniton(ds);

									elementMap.put(childName, ds);

								} else {
									DOMEvaluator.this.attributeMap
											.put(url + childName,
													new ObjectReference(
															bundle,
															childElement
																	.getAttribute("class").trim()));
								}
							}
						}
					} catch (final SAXException e1) {
						Activator.logError("Exception during parsing:", name
								+ "/" + attribute, e1);
					} catch (final IOException e1) {
						Activator.log(e1);
					}
				} else {
					Activator.logError(
							"com.onpositive.commons.platform",
							"namespace definition file:" + attribute
									+ " was not parsed "
									+ bundle.getSymbolicName(), null);
					throw new RuntimeException();
				}
			}
		}

		private HashMap<String, ObjectReference> loadImplementation() {
			IConfigurationElement[] configurationElementsFor = Platform
					.getExtensionRegistry()
					.getConfigurationElementsFor(
							"com.onpositive.commons.platform.namespaceImplementation");

			HashMap<String, ObjectReference> lm = new HashMap<String, ObjectReference>();

			for (IConfigurationElement ce : configurationElementsFor) {
				String ceUrl = ce.getAttribute("url");
				String contributorId = ce.getContributorId();
				Bundle bundle = Platform.getBundle(contributorId);

				if (ceUrl != null && ceUrl.equals(url)) {
					IConfigurationElement[] children = ce.getChildren();
					for (IConfigurationElement ceChild : children) {
						String elementName = ceChild
								.getAttribute("elementName");
						if (ceChild.getName().equals("handler")) {
							// lm.put(ceChild.getAttribute("elementName"), new
							// GeneralElementHandler( bundle,
							// ceChild.getAttribute("class")) );
							lm.put(elementName, new ObjectReference(bundle,
									ceChild.getAttribute("class").trim()));
						}
						if (ceChild.getName().equals("mapping")) {
							lm.put(elementName, new BasicHandlerReference(
									bundle, ceChild.getAttribute("class").trim()));
							// lm.put(ceChild.getAttribute("elementName"), new
							// CloningHandler ( bundle,
							// ceChild.getAttribute("class")) );
						}
						if (ceChild.getName().equals("annotation")) {
							lm.put(elementName, new GeneralAnnotationHandler(
									bundle, ceChild.getAttribute("class").trim()));
						}
					}
				}
			}
			return lm;
		}

		public void checkLoad() {
			if (!loaded) {
				load();
				loaded = true;
			}
		}

		public void validate() {
			checkLoad();
			for (ElementDefinition ed : elementMap.values()) {
				if (!ed.isAbstract) {
					ArrayList<AttributeDefinition> supportedAttrs = new ArrayList<AttributeDefinition>();

					supportedAttrs.addAll(ed.supportedAttrs);

					ElementDefinition[] extendedElements = ed
							.getExtendedElements();
					for (int i = 0; i < extendedElements.length; i++)
						supportedAttrs
								.addAll(extendedElements[i].supportedAttrs);

					ed.checkAttributeSupport(supportedAttrs);
					ed.checkChildrenSupport();
				}
			}
		}

		ElementDefinition getElement(String elementName) {
			checkLoad();
			return elementMap.get(elementName);
		}

	}

	public static void evaluateChildren(Element element, Object parent,
			Context ctx) {

		final NodeList childNodes = element.getChildNodes();
		for (int a = 0; a < childNodes.getLength(); a++) {
			final Node item = childNodes.item(a);
			if (item instanceof Element)
				DOMEvaluator.getInstance()
						.evaluate((Element) item, parent, ctx);
		}
	}

	private final HashMap<String, NameSpace> namespaces = new HashMap<String, NameSpace>();

	Bundle evaluatorBundle;

	private DOMEvaluator() throws ParserConfigurationException {

		final DocumentBuilderFactory newInstance = DocumentBuilderFactory
				.newInstance();
		// newInstance.setXIncludeAware(true);
		newInstance.setNamespaceAware(true);
		this.builder = newInstance.newDocumentBuilder();
		final IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						"com.onpositive.commons.platform.namespaces"); //$NON-NLS-1$

		this.evaluatorBundle = Platform
				.getBundle("com.onpositive.commons.platform"); //$NON-NLS-1$
		for (final IConfigurationElement ce : configurationElements) {
			final String attribute = ce.getAttribute("url"); //$NON-NLS-1$
			if (attribute == null)
				throw new IllegalArgumentException(
						"Extension with null namespace " + ce.getContributorId()); //$NON-NLS-1$

			NameSpace nameSpace = this.namespaces.get(attribute);
			if (nameSpace == null) {
				nameSpace = new NameSpace(ce, attribute);
				this.namespaces.put(attribute, nameSpace);
			}
		}
	}

	void validateNamespaces() {
		if (!Platform.isDebug())
			return;

		for (NameSpace ns : namespaces.values())
			ns.validate();
	}

	// public Object evaluateLocalPluginResource(Plugin pl, String path,
	// Object pContext) throws Exception {
	// final URL findResource = pl.getBundle().getEntry(path);
	// if (findResource == null) {
	// throw new IllegalArgumentException(
	//					"resource " + path + " is not found in bundle " + pl.getBundle().getSymbolicName()); //$NON-NLS-1$ //$NON-NLS-2$
	// }
	// IAbstractConfiguration pluginPreferences = IAbstractConfiguration
	// .getPluginPreferences(pl);
	// pluginPreferences = (IAbstractConfiguration) pluginPreferences
	// .createSubConfiguration(path);
	// return this.evaluate(findResource, pContext, pl.getBundle().getClass()
	// .getClassLoader(), path, pluginPreferences);
	// }

	public Object evaluateLocalPluginResource(final Bundle pl, String path,
			Object pContext) throws Exception {

		final IResourceLink findResource = pl.getEntry(path);
		if (findResource == null) {
			throw new IllegalArgumentException(
					"resource " + path + " is not found in bundle " + pl.getSymbolicName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		IAbstractConfiguration pluginPreferences = evaluatorBundle
				.getPreferences();
		pluginPreferences = (IAbstractConfiguration) pluginPreferences
				.createSubConfiguration(path);

		return this.evaluate(findResource.openStream(), pContext,
				new ClassLoader() {

					public Class<?> loadClass(String name)
							throws ClassNotFoundException {
						return pl.loadClass(name);
					}

					@Override
					public InputStream getResourceAsStream(String name) {
						return pl.getResourceAsStream(name);
					}

					protected synchronized Class<?> loadClass(String name,
							boolean resolve) throws ClassNotFoundException {
						return pl.loadClass(name);
					}

				}, path, pluginPreferences);
	}

	public Object evaluateClassResource(Class<?> cl, String path,
			Object pContext) throws Exception {

		final InputStream findResource = cl.getResourceAsStream(path);
		if (findResource == null)
			throw new IllegalArgumentException(
					"resource " + path + " is not found on classpath of " + cl.getName()); //$NON-NLS-1$ //$NON-NLS-2$

		IAbstractConfiguration pluginPreferences = this.evaluatorBundle
				.getPreferences();
		pluginPreferences = (IAbstractConfiguration) pluginPreferences
				.createSubConfiguration(path);
		return this.evaluate(findResource, pContext, cl.getClassLoader(), path,
				pluginPreferences);
	}

	public Object evaluateLocalPluginResource(Class<?> cls, String path,
			Object pContext) throws Exception {
		final ClassLoader classLoader = cls.getClassLoader();
		final InputStream findResource = classLoader.getResourceAsStream(path);

		if (findResource == null)
			throw new IllegalArgumentException(
					"resource " + path + " is not found in plugin loaded  " + cls.getName()); //$NON-NLS-1$ //$NON-NLS-2$

		IAbstractConfiguration pluginPreferences = evaluatorBundle
				.getPreferences();
		pluginPreferences = (IAbstractConfiguration) pluginPreferences
				.createSubConfiguration(path);
		return this.evaluate(findResource, pContext, classLoader, path,
				pluginPreferences);
	}

	public Object evaluate(InputStream url, Object pContext,
			ClassLoader loader, String bundlePath, IAbstractConfiguration config)
			throws Exception {
		InputStream openStream = url;
		final Context context = new Context(bundlePath);
		context.setId(bundlePath);
		context.setConfiguration(config);
		context.setClassLoader(loader);
		final String uri = url.toString();
		return evaluate(pContext, openStream, context, uri);
	}

	protected ThreadLocal<Context>contextLocal=new ThreadLocal<Context>();
	
	public static Context getContext(){
		return getInstance().contextLocal.get();
	}
	public Object evaluate(Object pContext, InputStream openStream,
			final Context context, final String uri) throws SAXException,
			IOException {
		try {
			contextLocal.set(context);
			final Document parse = this.builder.parse(openStream);
			final Element documentElement = parse.getDocumentElement();

			this.proceedExternalisation(parse.getDocumentElement(), context);
			final Object evaluate = this.evaluate(documentElement, pContext,
					context);

			context.performInit();
			return evaluate;
		} finally {
			contextLocal.set(null);
			openStream.close();
		}
	}

	private void proceedExternalisation(Element documentElement, Context context) {
		final NamedNodeMap attributes = documentElement.getAttributes();
		final int attributesCount = attributes.getLength();
		for (int a = 0; a < attributesCount; a++) {
			final Node item = attributes.item(a);

			final String nodeValue = item.getNodeValue();
			if (nodeValue.length() > 0) {
				final char ch = nodeValue.charAt(0);
				if (ch == '%') {
					final String newValue = context.externalize(nodeValue
							.substring(1));
					item.setNodeValue(newValue);
				}
			}
		}
		final NodeList childNodes = documentElement.getChildNodes();
		final int childNodesCount = childNodes.getLength();
		for (int a = 0; a < childNodesCount; a++) {
			final Node k = childNodes.item(a);
			if (k instanceof Element) {
				this.proceedExternalisation((Element) k, context);
			}
		}
	}

	public Object evaluate(Element documentElement, Object pContext, Context ctx) {
		final NamedNodeMap attributes = documentElement.getAttributes();
		final int attributesCount = attributes.getLength();

		final String namespaceURI = documentElement.getNamespaceURI();

		if (namespaceURI == null)
			throw new IllegalArgumentException(
					"Element with null namespace when processing url " + ctx.getUri()); //$NON-NLS-1$

		final NameSpace nameSpace = this.namespaces.get(namespaceURI);
		if (nameSpace != null) {
			nameSpace.checkLoad();
			final ElementDefinition elementGenericRegistryObject = nameSpace.elementMap
					.get(documentElement.getLocalName());

			if (elementGenericRegistryObject != null) {
				IElementHandler elementHandler;
				// String elementName = documentElement.getLocalName() ;
				elementHandler = (IElementHandler) elementGenericRegistryObject.reference
						.getObject();

				if (elementHandler == null) {
					throw new IllegalArgumentException("Unknown element:"
							+ namespaceURI + "#"
							+ documentElement.getLocalName());
				}

				final Object handledElement = elementHandler.handleElement(
						documentElement, pContext, ctx);

				if (!this.attributeMap.isEmpty()) {
					for (int a = 0; a < attributesCount; a++) {
						final Node attrItem = attributes.item(a);
						final String key = attrItem.getNamespaceURI()
								+ attrItem.getLocalName();
						final ObjectReference attributeGenericRegistryObject = this.attributeMap
								.get(key);

						if (attributeGenericRegistryObject != null) {
							final IAttributeHandler attributeHandler = (IAttributeHandler) attributeGenericRegistryObject
									.getObject();
							attributeHandler.handleAttribute(handledElement,
									attrItem.getNodeValue(), ctx);
						}
					}
				}
				final String idAttributeValue = documentElement
						.getAttribute("id"); //$NON-NLS-1$
				if ((idAttributeValue != null)
						&& (idAttributeValue.length() > 0)
						&& (elementHandler != null)) {
					ctx.register(idAttributeValue, handledElement);
				}
				return handledElement;
			}
			throw new IllegalStateException(
					"Handler for element " + documentElement.getLocalName() + " not found in namespace " + namespaceURI + "\nKnown Elements: " + nameSpace.elementMap.keySet()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		throw new IllegalStateException(
				"Namespace " + namespaceURI + " not found in the registry" + "( known namespaces are:" + namespaces.keySet() + ")"); //$NON-NLS-1$ //$NON-NLS-2$		
	}

	public static ElementDefinition getElement(String elementName,
			String namespace) {
		NameSpace ns = DOMEvaluator.getInstance().namespaces.get(namespace);
		return ns != null ? ns.getElement(elementName) : null;
	}

	private static DOMEvaluator instance;
	private final DocumentBuilder builder;

	public static DOMEvaluator getInstance() {
		if (instance == null) {
			try {
				instance = new DOMEvaluator();
				instance.validateNamespaces();
			} catch (final ParserConfigurationException e) {
				throw new IllegalStateException(e);
			}
		}
		return instance;
	}

	protected static ServiceMap<NoCacheServiceObject<IExpressionController>> expressionControllersMap = new ServiceMap<NoCacheServiceObject<IExpressionController>>(
			"com.onpositive.commons.platform.expressionController",
			NoCacheServiceObject.class);

	public static IExpressionController getExpressionController(
			Class<?> elementClass) {
		IExpressionController objectAttribute = null;
		ServiceObject<IExpressionController> serviceObject = expressionControllersMap
				.get(elementClass);
		if (serviceObject == null) {
			System.err.println("Expression controller not found for:"
					+ elementClass);
			return null;
		}
		objectAttribute = serviceObject.getService();
		return objectAttribute;
	}
}
