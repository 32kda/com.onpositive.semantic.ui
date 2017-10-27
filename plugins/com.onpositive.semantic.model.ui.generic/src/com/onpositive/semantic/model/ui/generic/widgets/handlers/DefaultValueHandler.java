package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.Binding;

public class DefaultValueHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		Binding bnd = (Binding) parentContext;
		if (bnd.getValue() == null) {
			String attribute = element.getAttribute("value");
			String type = element.getAttribute("type");
			if (attribute.equals("new")) {
				Object newInstance = context.newInstance(type);
				bnd.setValue(newInstance, null);
			} else {
				if (type.length() > 0) {
					try {
						Class<?> loadClass = context.getClassLoader()
								.loadClass(type);
						Method method = loadClass.getMethod("valueOf",
								new Class[] { String.class });
						Object invoke = method.invoke(null, attribute);
						bnd.setValue(invoke, null);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					if (attribute.length() > 0) {
						bnd.setValue(attribute, null);
					}
				}
			}
		}
		return null;
	}

}
