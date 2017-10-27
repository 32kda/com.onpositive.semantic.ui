package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.elements.SashElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler.UIHandlerStruct;

public final class SashHandlerStruct extends UIHandlerStruct {
	

	public SashHandlerStruct() {
		super("com.onpositive.commons.elements.SashElement");
	}

	public Object newInstance(Element element, Object parent) {
		final SashElement newInstance = (SashElement) super
				.newInstance(element, parent);
		final boolean parseBoolean = Boolean
				.parseBoolean(element
						.getAttribute("horizontal"));
		newInstance.setHorizontal(parseBoolean); //$NON-NLS-1$
		final String attribute = element
				.getAttribute("weights"); //$NON-NLS-1$
		if (attribute.length() > 0) {
			final String[] ww = attribute.split(","); //$NON-NLS-1$
			final int[] actualWeights = new int[ww.length];
			for (int a = 0; a < ww.length; a++) {
				actualWeights[a] = Integer.parseInt(ww[a]);
			}
			newInstance.setWeights(actualWeights);
		}
		return newInstance;
	}
}