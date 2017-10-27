package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler.UIHandlerStruct;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;

public final class FormEditorHandlerStruct extends UIHandlerStruct {

	public FormEditorHandlerStruct() {
		super("com.onpositive.semantic.model.ui.property.editors.FormEditor");
	}

	public Object newInstance(Element element, Object parent) {
		final FormEditor ed = (FormEditor) super
				.newInstance(element, parent);
		final String at = element
				.getAttribute("showValueInTitle");
		if (at.length() > 0) {
			ed.setBindingInTitle(Boolean
					.parseBoolean(at));
		}
		return ed;
	}
}