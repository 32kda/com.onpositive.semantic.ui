package com.onpositive.semantic.ui.xml;

import org.eclipse.swt.SWT;
import org.w3c.dom.Element;

import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;

public class CheckboxHandler extends UIElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		if (element.getLocalName().equals("checkbox")) { //$NON-NLS-1$
			return new ButtonSelector(SWT.CHECK);
		}
		return new ButtonSelector(SWT.RADIO);
	}
}
