package com.onpositive.semantic.ui.xml;

import org.eclipse.jface.viewers.ILabelProvider;
import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.EditableListEnumeratedValueSelector;

public class ListSelectionHandler extends UIElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName, Context ctx){
		EditableListEnumeratedValueSelector createElement = (EditableListEnumeratedValueSelector) super.createElement(element, parentContext, localName, ctx);
		String attribute = element.getAttribute("labelProvider"); //$NON-NLS-1$
		if (attribute.length()!=0){
			createElement.setLabelProvider((ILabelProvider) ctx.newInstance(attribute));
		}
		return createElement;
	}
	
	protected Object createElement(Element element, Object parentContext,
			String localName) {
		final EditableListEnumeratedValueSelector listEnumeratedValueSelector = new EditableListEnumeratedValueSelector();
		
		String attribute = element.getAttribute("linkSelectionWith"); //$NON-NLS-1$
		listEnumeratedValueSelector.setValueAsSelection(Boolean
				.parseBoolean(element.getAttribute("useSelectionAsValue"))); //$NON-NLS-1$
		if ((attribute != null) && (attribute.length() > 0)) {
			listEnumeratedValueSelector
					.addElementListener(new EditorBindingController(
							listEnumeratedValueSelector, attribute) {

						protected void install(IBinding binding) {
							listEnumeratedValueSelector
									.setSelectionBinding(binding);
						}

					});
		}		
		attribute = element.getAttribute("isOrdered"); //$NON-NLS-1$
		if (attribute.length()!=0){
			listEnumeratedValueSelector.setOrdered(Boolean.parseBoolean(attribute));
		}
		attribute = element.getAttribute("directEditProperty");
		
		if (attribute.length() > 0) {
			listEnumeratedValueSelector.setDirectCellEditProperty(attribute);
		}
		return listEnumeratedValueSelector;
	}
}
