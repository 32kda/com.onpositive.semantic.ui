package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.editactions.PropertyContributionManager;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class EditMenuHandler extends AbstractActionElementHandler{
	
	private static final class EditSelectionListener implements ISelectionListener {
		
		private final PropertyContributionManager contributionManager;
		private IListElement<?> list;

		public EditSelectionListener(PropertyContributionManager contributionManager, IListElement<?> control) {
			this.contributionManager = contributionManager;
			this.list=control;
		}

		@Override
		public void selectionChanged(IStructuredSelection selection) {
			contributionManager.setBaseObjects(selection.toList().toArray(),list.getBinding());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IContributionItem contribute(ActionsSetting parentContext,
			Context context, Element element) {
		PropertyContributionManager contributionManager = new PropertyContributionManager();
		final IListElement<?> control = (IListElement<Object>) parentContext.getControl();
		control.addSelectionListener(new EditSelectionListener(contributionManager,control));
		handleAction(element, parentContext, contributionManager,parentContext.getControl());
		return contributionManager;
	}

}
