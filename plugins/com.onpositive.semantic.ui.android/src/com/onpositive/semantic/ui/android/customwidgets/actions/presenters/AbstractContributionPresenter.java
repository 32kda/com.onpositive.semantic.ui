package com.onpositive.semantic.ui.android.customwidgets.actions.presenters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.onpositive.semantic.editactions.OwnedAction;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.actions.IObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;


public abstract class AbstractContributionPresenter implements
		IContributionPresenter {

	public AbstractContributionPresenter() {
		super();
	}

	@Override
	public void presentContributionItem(IContributionItem item,
			View anchor) {
		if (item instanceof IContributionManager) {
			IContributionItem[] items = ((IContributionManager) item)
					.getItems();
			presentManagerItems(item, anchor, items);
		}
	}
	
	@Override
	public void presentObjectContributionItem(
			IObjectContributionManager manager, IStructuredSelection selection,
			View anchor) {
		IContributionItem[] items = manager.getItems(selection);
		presentManagerItems((IContributionItem) manager, anchor, items);
	}

	protected void presentManagerItems(IContributionItem item, View anchor,
			 IContributionItem[] childItems) {
		List<IContributionItem> enabledItems = new ArrayList<IContributionItem>();
		for (IContributionItem item2 : childItems) {
			boolean enabled = item2.isEnabled();
			if (item2 instanceof OwnedAction) {
				enabled = ((OwnedAction) item2).isActuallyEnabled(); //TODO may be incorrect
			}
			if (enabled) {
				enabledItems.add(item2);
			}
		}
		if (enabledItems.size() == 0) {
			return;
		}
		if (enabledItems.size() == 1) {
			this.processSingle(enabledItems.get(0), anchor);
			return;
		}
		this.doPresent(item, 
				enabledItems.toArray(new IContributionItem[enabledItems.size()]),
				anchor);
	}

	protected abstract void doPresent(IContributionItem item,
			IContributionItem[] enabled,
			View anchor);

	@Override
	public void processSingle(IContributionItem item, View anchor) {
		if (item instanceof IAction) {
			((IAction) item).run();
		} else if (item instanceof IContributionManager) { 
			presentContributionItem(item, anchor); //TODO support different presenters for different submenu levels
		}
	}

}