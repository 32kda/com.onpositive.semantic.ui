package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.editactions.PropertyContributionManager;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class InlinedEditMenuHandler extends EditMenuHandler {

	
	@Override
	protected HierarchyController createHierarcyController(
			Object parentContext, Context context, Element element) {
		return new InlinedMenuHierarcyController(element, parentContext, context, this);
	}
	
	@Override
	protected IContributionItem contribute(ActionsSetting parentContext,
			Context context, Element element) {
		PropertyContributionManager contributionManager = new PropertyContributionManager();
		handleAction(element, parentContext, contributionManager,parentContext.getControl());
		return contributionManager;
	}
	
}
