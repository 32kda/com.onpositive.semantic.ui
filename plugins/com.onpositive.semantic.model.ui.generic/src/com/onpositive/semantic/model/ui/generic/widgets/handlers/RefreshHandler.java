package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class RefreshHandler extends AbstractActionElementHandler {

	public RefreshHandler() {
	}

	
	protected Action contribute(ActionsSetting parentContext, Context context,
			Element element) {
		final IListElement<Object> vl = (IListElement<Object>) parentContext.getControl();
		final IContributionItem action = (IContributionItem) vl.createRefreshContributionItem();			
//		if (attribute.length()>0){
//			basicAction.setDirectEdit(Boolean.parseBoolean(attribute));
//		}
//		if (role.length()>0){
//			basicAction.setRole(role);
//		}
//		if (theme.length()>0){
//			basicAction.setTheme(theme);
//		}
//		if (widget.length()>0){
//			basicAction.setWidgetId(widget);
//		}
		handleAction(element, parentContext, (Action) action,parentContext.getControl());
		//parentContext.addAction(action,element);
		return (Action)action ;
	}

	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}

}