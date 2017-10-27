package com.onpositive.semantic.model.data;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.AnnotationProxy;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.api.property.IRealmPropertyConfigurer;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.adapters.RegexpValidator;
import com.onpositive.semantic.model.api.property.java.annotations.Range;

public class DataModelEvaluator implements IElementHandler {

	public DataModelEvaluator() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final String localName = element.getLocalName();
		final String id = element.getAttribute("id"); //$NON-NLS-1$
		if (localName.equals("regexp-constraint")) {
			final RegexpValidator vl = new RegexpValidator(element
					.getAttribute("pattern"), element
					.getAttribute("description"));
			final ValueClass vClass = (ValueClass) parentContext;
			vClass.addValidator(vl);
		} else if (localName.equals("realm-provider")) {
			final ValueClass vClass = (ValueClass) parentContext;
			try {
				final Class<? extends IRealmProvider<?>> pla = (Class<? extends IRealmProvider<?>>) context
						.getClassLoader().loadClass(
								element.getAttribute("class"));
				try {
					vClass.registerAdapter(IRealmProvider.class, pla
							.newInstance());
				} catch (final InstantiationException e) {
					Activator.log(e);
				} catch (final IllegalAccessException e) {
					// TODO Auto-generated catch block
					Activator.log(e);
				}
			} catch (final ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Activator.log(e);
			}
		} else if (localName.equals("instanceListener")) {
			final ValueClass vClass = (ValueClass) parentContext;
			IInstanceListener newInstance = (IInstanceListener) context.newInstance(element.getAttribute("class"));
			vClass.addInstanceListener(newInstance);
		} else if (localName.equals("range-constraint")) {
			final AnnotationProxy<Range> rm = new AnnotationProxy<Range>(
					Range.class, context.getClassLoader(), element);
			final ValueClass vClass = (ValueClass) parentContext;
			vClass.registerAdapter(Range.class, rm.getInstance());
		} else if (localName.equals("model")) { //$NON-NLS-1$

			if (parentContext instanceof DataModel) {
				DOMEvaluator.evaluateChildren(element, parentContext, context);
				return parentContext;
			} else {
				final DataModel ma = new DataModel();
				ma.addClassLoader(context.getClassLoader());
				DOMEvaluator.evaluateChildren(element, ma, context);
				return ma;
			}
		} else {
			boolean equals = localName.equals("calculatableProperty");
			boolean equals2= localName.equals("inverseProperty");
			if (localName.equals("property") || equals||equals2) { //$NON-NLS-1$
				final DataModel parent = (DataModel) parentContext;
				final DefaultModelProperty pa = new DefaultModelProperty(id);
				pa.setName(element.getAttribute("caption")); //$NON-NLS-1$
				pa.setDescription(element.getAttribute("description")); //$NON-NLS-1$
				final Boolean parseBoolean = this.parseBoolean(element,
						"readonly"); //$NON-NLS-1$
				if (parseBoolean != null) {
					pa.setReadOnly(parseBoolean);
				}
				int minCardinality = 0;
				int maxCardinality = 1;
				Integer parseInteger = this.parseInteger(element,
						"minCardinality"); //$NON-NLS-1$
				if (parseInteger != null) {
					minCardinality = parseInteger;
				}
				parseInteger = this.parseInteger(element, "maxCardinality"); //$NON-NLS-1$
				if (parseInteger != null) {
					maxCardinality = parseInteger;
				}
				String attribute = element.getAttribute("unique");
				if (attribute.length() != 0) {
					boolean parseBoolean2 = Boolean.parseBoolean(attribute);
					if (parseBoolean2) {
						pa.setUnique(parseBoolean2);
					}
				}
				pa.setMinCardinality(minCardinality);
				pa.setMaxCardinality(maxCardinality);
				if (equals) {
					Object newInstance = context.newInstance(element
							.getAttribute("class"));
					pa.setCalculator((ICalculatableProperty) newInstance);
				}
				if (equals2){
					String attribute2 = element.getAttribute("of");
					pa.setCalculator(new InversePropertyCalculator(attribute2));
				}
				pa.setRange(element.getAttribute("range")); //$NON-NLS-1$
				pa.setDomain(element.getAttribute("domain")); //$NON-NLS-1$
				String atr = element.getAttribute("defaultValue");
				if (atr.length() > 0) {
					pa.setDefaultValueString(atr);
				}
				atr = element.getAttribute("groupValueProvider");
				if (atr.length() > 0) {
					try {
						final Class<?> clazz = context.getClassLoader()
								.loadClass(atr);
						try {
							final Object newInstance = clazz.newInstance();
							pa.registerAdapter(IRealmPropertyConfigurer.class,
									(IRealmPropertyConfigurer) newInstance);
						} catch (final InstantiationException e) {
							// TODO Auto-generated catch block
							Activator.log(e);
						} catch (final IllegalAccessException e) {
							// TODO Auto-generated catch block
							Activator.log(e);
						}
					} catch (final ClassNotFoundException e) {
						// TODO Auto-generated catch block
						Activator.log(e);
					}
				}
				parent.registerProperty(pa);
			} else if (localName.equals("class")) { //$NON-NLS-1$
				final DataModel parent = (DataModel) parentContext;
				final ValueClass vclass = new ValueClass(parent);
				vclass.setId(id);
				vclass.setSuperClassesString(element
						.getAttribute("superClasses")); //$NON-NLS-1$
				String attribute = element.getAttribute("broadCastChanges");
				if (attribute.length() > 0) {
					vclass.setBroadCastChanges(Boolean.valueOf(attribute));
				}
				vclass.setName(element.getAttribute("caption"));
				parent.registerClass(vclass);
				DOMEvaluator.evaluateChildren(element, vclass, context);
			} else if (localName.equals("include")) { //$NON-NLS-1$

			}
		}
		return null;
	}

	private Boolean parseBoolean(Element element, String name) {
		Boolean vl = null;
		final String attribute = element.getAttribute(name);
		if ((attribute != null) && (attribute.length() > 0)) {
			final boolean parseBoolean = Boolean.parseBoolean(attribute);
			vl = parseBoolean;
		}
		return vl;
	}

	private Integer parseInteger(Element element, String name) {
		Integer vl = null;
		final String attribute = element.getAttribute(name);
		if ((attribute != null) && (attribute.length() > 0)) {
			if (attribute.charAt(0) == '*') {
				return Integer.MAX_VALUE;
			}
			final int parseBoolean = Integer.parseInt(attribute);
			vl = parseBoolean;
		}
		return vl;
	}
}