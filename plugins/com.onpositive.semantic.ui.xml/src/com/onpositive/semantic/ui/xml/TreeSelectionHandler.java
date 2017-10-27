package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.viewer.structured.TreeContentProvider;

public class TreeSelectionHandler extends ListSelectionHandler {

	
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		ListEnumeratedValueSelector<?> createElement = (ListEnumeratedValueSelector<?>) super.createElement(element, parentContext, localName);
		createElement.setAsTree(true);		
		TreeContentProvider provider = new TreeContentProvider();
		provider.setProperty(element.getAttribute("property"));
		createElement.setContentProvider(provider);
		return createElement;
	}
}
