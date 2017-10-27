package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.elements.ScrollableContainer;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;

public class ScrollableHandler extends UIElementHandler{

	
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		ScrollableContainer scrollableContainer = new ScrollableContainer();
		String attribute = element.getAttribute("alwaysShowScrollBars");
		if (attribute.length()>0){
			scrollableContainer.setAlwaysShowScrollBars(Boolean.parseBoolean(attribute));
		}
		attribute = element.getAttribute("expandHorizontal");
		if (attribute.length()>0){
			scrollableContainer.setExpandHorizontal(Boolean.parseBoolean(attribute));
		}
		attribute = element.getAttribute("expandVertical");
		if (attribute.length()>0){
			scrollableContainer.setExpandVertical(Boolean.parseBoolean(attribute));
		}
		
		return scrollableContainer;
	}

}
