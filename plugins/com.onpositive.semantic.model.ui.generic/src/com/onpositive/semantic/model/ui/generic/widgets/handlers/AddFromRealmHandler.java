package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class AddFromRealmHandler extends AbstractActionElementHandler {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -8464479089342837068L;

	public AddFromRealmHandler() {
	}

	
	protected Action contribute( ActionsSetting parentContext, Context context, Element element )
	{
		final IContributionItem basicAction = createAction( parentContext, element );
		parentContext.addAction( basicAction, element );
		return (BindedAction)basicAction ;
	}

	@SuppressWarnings("unchecked")
	private IContributionItem createAction( ActionsSetting parentContext, Element element )
	{
		final IListElement<Object> vl = (IListElement<Object> )parentContext.getControl();		
		final Action bindedAction = (Action)vl.createAddFromContributionItem();
		handleAction( element, parentContext, bindedAction, parentContext.getControl() );		
		return bindedAction;
	}
		
//	protected void contribute( IProvidesToolbarManager parentContext, Context context, Element element)
//	{
//		throw new UnsupportedOperationException("Works only with actions tag") ;
//	}
}
