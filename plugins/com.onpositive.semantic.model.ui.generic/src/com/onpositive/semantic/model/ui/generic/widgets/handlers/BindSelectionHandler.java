package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import com.onpositive.commons.xml.language.AttributeHandler;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.generic.SelectionBindingController;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;

@SuppressWarnings("rawtypes")
public final class BindSelectionHandler extends
		AttributeHandler<IListElement> {
	public BindSelectionHandler() {
		super(IListElement.class);
	}

	@SuppressWarnings("unchecked")
	public void handle(IListElement element,
			Object context, String value, Context ctx) {
		element.addElementListener(new SelectionBindingController(element, value));
	}
}