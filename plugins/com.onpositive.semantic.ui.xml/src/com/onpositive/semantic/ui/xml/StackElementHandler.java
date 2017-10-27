package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.elements.StackElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;

public class StackElementHandler extends UIElementHandler{

	@Override
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		return new StackElement();
	}
	
}
