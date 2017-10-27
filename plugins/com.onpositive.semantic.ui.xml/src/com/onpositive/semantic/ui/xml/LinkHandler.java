package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.elements.LinkElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;

public class LinkHandler extends UIElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		LinkElement linkElement = new LinkElement();
		linkElement.setUrl(element.getAttribute("url"));
		return linkElement;
	}
	
}
