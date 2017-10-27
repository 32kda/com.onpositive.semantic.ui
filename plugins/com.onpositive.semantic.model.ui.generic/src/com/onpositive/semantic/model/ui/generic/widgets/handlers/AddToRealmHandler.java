package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.generic.widgets.IAddNewAction;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class AddToRealmHandler extends AbstractActionElementHandler {

	public AddToRealmHandler() {
	}

	
	@SuppressWarnings("unchecked")	
	protected Action contribute( ActionsSetting parentContext, Context context,
			Element element) {
		final IListElement<Object> vl = (IListElement<Object>) parentContext.getControl();
		final IAddNewAction action = (IAddNewAction)vl.createAddNewContributionItem();
		handleAction(element, parentContext, (Action) action,parentContext.getControl());
		action.setTypeId(element.getAttribute("targetType"));
		action.setWidgetId(element.getAttribute("theme"));
		final String attribute = element.getAttribute("objectClass");
		if (attribute.length() > 0) {
			try {
				action.setObjectClass(context.getClassLoader().loadClass(
						attribute));
			} catch (final ClassNotFoundException e) {
				Platform.log(e);
			}
		}
		if (element.getNodeName().equals("create-child")){
			action.setCreateChild(true);
		}
		return (Action) action ;
	}

	

	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}
//	
}
