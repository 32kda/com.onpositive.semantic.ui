package com.onpositive.semantic.ui.xml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Element;

import com.onpositive.commons.elements.UniversalUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;

public class SeparatorHandler extends UIElementHandler{

	
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		boolean vertical=element.getAttribute("vertical").equals("true");
		UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(Label.class,SWT.SEPARATOR|(vertical?SWT.VERTICAL:SWT.HORIZONTAL));
		if (vertical){
			universalUIElement.getLayoutHints().setGrabVertical(true);
			universalUIElement.getLayoutHints().setAlignmentVertical(SWT.FILL);
		}
		else{
			universalUIElement.getLayoutHints().setGrabVertical(false);
			universalUIElement.getLayoutHints().setAlignmentHorizontal(SWT.FILL);
		}
		return universalUIElement;
	}
}
