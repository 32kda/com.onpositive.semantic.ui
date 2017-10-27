package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;

public class GroupHandler extends UIElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		final Container container = new Container(Container.GROUP);
		if (element.getLocalName().equals("hgroup")) { //$NON-NLS-1$			
			container.setLayoutManager(new HorizontalLayouter());
		}
		container.setLayoutManager(new OneElementOnLineLayouter());
		return container;
	}
}
