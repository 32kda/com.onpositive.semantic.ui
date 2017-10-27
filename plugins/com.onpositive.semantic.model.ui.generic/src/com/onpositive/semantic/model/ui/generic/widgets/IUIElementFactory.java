package com.onpositive.semantic.model.ui.generic.widgets;

import org.w3c.dom.Element;


public interface IUIElementFactory {

	 
	 Object newUIElement(Class<?> class1, String localName,
			Element element, Object parentContext);	

}
