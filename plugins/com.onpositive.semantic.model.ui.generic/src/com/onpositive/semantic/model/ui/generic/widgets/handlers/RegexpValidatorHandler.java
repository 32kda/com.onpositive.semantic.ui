package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.api.validation.RegexpValidator;
import com.onpositive.semantic.model.binding.Binding;

public class RegexpValidatorHandler implements IElementHandler {

	public RegexpValidatorHandler() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		if (parentContext instanceof Binding) {
			final Binding bs = (Binding) parentContext;
			bs.addValidator(new RegexpValidator(element.getAttribute("regexp"), element.getAttribute("message"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return parentContext;
	}

}
