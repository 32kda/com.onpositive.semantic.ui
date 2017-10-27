package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IRemoveAction;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class RemoveSelectedHandler extends AbstractActionElementHandler {

	public RemoveSelectedHandler() {
	}

	
	protected Action contribute(ActionsSetting parentContext, Context context,
			Element element) {
		final IListElement<?> vl = (IListElement<?>) parentContext
				.getControl();
		final IContributionItem action = vl.createRemoveSelectedContributionItem();
		IRemoveAction basicAction=(IRemoveAction) action;
		basicAction.setConfirmTitle(element.getAttribute("confirmTitle")); //$NON-NLS-1$
		basicAction.setConfirmDescription(element
				.getAttribute("confirmDescription")); //$NON-NLS-1$
		action.setEnabled(!vl.getViewerSelection().isEmpty());		
		handleAction(element, parentContext, (Action) action,parentContext.getControl());
//		parentContext.addAction( action, element );
		return (Action) action ;
	}
	
	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}
}
