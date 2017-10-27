package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class PasswordElementHandler extends UIElementHandler{

	
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		return new OneLineTextElement<Object>();
	}

	
	public Object handleElement(Element element, Object parentContext,
			Context ctx) {
		OneLineTextElement<?> handleElement = (OneLineTextElement<?>) super.handleElement(element, parentContext, ctx);
		handleElement.setIsPassword(true);
		return handleElement;
	}

}
