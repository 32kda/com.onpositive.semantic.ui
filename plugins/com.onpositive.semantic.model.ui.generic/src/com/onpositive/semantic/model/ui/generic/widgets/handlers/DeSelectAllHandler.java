package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class DeSelectAllHandler extends AbstractActionElementHandler {

	public DeSelectAllHandler() {
	}

	@SuppressWarnings("unchecked")
	
	protected Action contribute(ActionsSetting parentContext, Context context,
			Element element) {
		final IUIElement control = parentContext.getControl();
		final IPropertyEditor<?> c = (IPropertyEditor<?>) control.getService(IPropertyEditor.class);
		final Binding bnd = (Binding) c.getBinding();
		
		if( bnd == null )
			return null ;
		
		final ActionBinding binding = new ActionBinding() {

			public void doAction() {
				final IListElement<Object> ls = (IListElement<Object>) control;
				if (ls.isValueAsSelection()){
					final Collection<Object> currentValue = ls.getCurrentValue();
					final HashSet<Object> lsa = new HashSet<Object>(currentValue);
					ls.removeValues(lsa);
				}
				else{
					ls.setSelection(new StructuredSelection());
				}
			}
		};
		bnd.setBinding(element.getAttribute("id"), binding);
		final BindedAction bindedAction = new BindedAction(binding);
		handleAction(element, context, bindedAction,parentContext.getControl());
		parentContext.addAction( bindedAction, element );
		return bindedAction ;
	}

	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}
}
