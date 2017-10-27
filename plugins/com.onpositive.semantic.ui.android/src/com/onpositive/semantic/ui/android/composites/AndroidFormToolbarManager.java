package com.onpositive.semantic.ui.android.composites;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.ui.android.customwidgets.actionbar.ActionBar;

public class AndroidFormToolbarManager implements IProvidesToolbarManager {
	
	protected ActionBar actionBar;
	protected List<IContributionItem> contributionItems = new ArrayList<IContributionItem>(); 

	public AndroidFormToolbarManager() {
	}

	@Override
	public void addToToolbar(IContributionItem bindedAction) {
		contributionItems.add(bindedAction);
		if (actionBar != null) {
			actionBar.addAction(bindedAction);
		}
	}

	@Override
	public void removeFromToolbar(IContributionItem action) {
		contributionItems.remove(action);
		if (actionBar != null) {
			actionBar.removeAction(action);
		}
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public void setActionBar(ActionBar actionBar) {
		if (this.actionBar != null) {
			for (IContributionItem contributionItem : contributionItems) {
				this.actionBar.removeAction(contributionItem);
			}
		}
		this.actionBar = actionBar;
		if (this.actionBar != null) {
			for (IContributionItem contributionItem : contributionItems) {
				this.actionBar.addAction(contributionItem);
			}
		}
	}

}
