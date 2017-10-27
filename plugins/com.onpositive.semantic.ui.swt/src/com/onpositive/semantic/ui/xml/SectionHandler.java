package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.SectionEditor;

public class SectionHandler extends UIElementHandler implements IElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		final SectionEditor sectionEditor = new SectionEditor();
		String attribute = element.getAttribute("decorateTitle"); //$NON-NLS-1$
		if ((attribute != null) && (attribute.length() > 0)) {
			sectionEditor.setHasTitleBar(Boolean.parseBoolean(attribute));
		}
		String at = element.getAttribute("showValueInTitle");
		if (at.length() > 0) {
			sectionEditor.setBindingInTitle(Boolean.parseBoolean(at));
		}
		at = element.getAttribute("expandable");
		if (at.length() > 0) {
			sectionEditor.setExpandable(Boolean.parseBoolean(at));
		}
		attribute = element.getAttribute("expanded"); //$NON-NLS-1$
		if ((attribute != null) && (attribute.length() > 0)) {
			sectionEditor.setExpanded(Boolean.parseBoolean(attribute));
		}
		return sectionEditor;
	}
}
