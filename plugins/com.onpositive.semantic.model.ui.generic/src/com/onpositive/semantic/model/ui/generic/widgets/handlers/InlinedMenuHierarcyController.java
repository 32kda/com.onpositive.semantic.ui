package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.editactions.PropertyContributionManager;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;

public class InlinedMenuHierarcyController extends HierarchyController
		implements ISelectionListener {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1449604559466655619L;
	private IContributionItem[] currentItems;
	private IBindable b;

	protected InlinedMenuHierarcyController(Element element,
			Object parentContext, Context context,
			AbstractActionElementHandler parentHandler) {
		super(element, parentContext, context, parentHandler);
		if (parentContext instanceof IListElement) {
			((IListElement<?>) parentContext).addSelectionListener(this);
			b = (IBindable) parentContext;
		}
	}

	@Override
	protected void addToMenu(IContributionItem newAction) {
		if (newAction instanceof ContributionManager) {
			IContributionItem[] items = ((ContributionManager) newAction)
					.getItems();
			for (IContributionItem item : items) {
				super.addToMenu(item);
			}
		} else
			super.addToMenu(newAction);
	}

	@Override
	protected void addToToolbar(IContributionItem newAction) {
		if (newAction instanceof ContributionManager) {
			IContributionItem[] items = ((ContributionManager) newAction)
					.getItems();
			for (IContributionItem item : items) {
				super.addToToolbar(item);
			}
		} else
			super.addToToolbar(newAction);

	}
	
	@Override
	protected void removeFromToolbar(IContributionItem action) {
		if (action instanceof ContributionManager) {
			IContributionItem[] items = ((ContributionManager) action)
					.getItems();
			for (IContributionItem item : items) {
				super.removeFromToolbar(item);
			}
		} else
			super.removeFromToolbar(action);

	}

	@Override
	protected void removeFromMenu(IContributionItem action) {
		if (action instanceof ContributionManager) {
			IContributionItem[] items = ((ContributionManager) action)
					.getItems();
			for (IContributionItem item : items) {
				super.removeFromMenu(item);
			}
		} else {
			super.removeFromMenu(action);
		}
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		Object[] selectedObjects = selection.toList().toArray();
		if (action instanceof PropertyContributionManager) {
			if (currentItems != null) {
				for (IContributionItem item : currentItems) {
					if (toMenu && popupMenuManager != null)
						super.removeFromMenu(item);
					if (toToolbar && toolbarManager != null)
						super.removeFromToolbar(item);
				}
			}
			((PropertyContributionManager) action).setBaseObjects(selectedObjects, b.getBinding());
			if (action instanceof IObjectContributionManager) {
				currentItems = ((IObjectContributionManager) action).getItems(selection);
			} else {
				currentItems = ((ContributionManager) action)
					.getItems();
			}
			for (IContributionItem item : currentItems) {
				if (toMenu && popupMenuManager != null)
					super.addToMenu(item);
				if (toToolbar && toolbarManager != null)
					super.addToToolbar(item);
			}
		}
	}

}
