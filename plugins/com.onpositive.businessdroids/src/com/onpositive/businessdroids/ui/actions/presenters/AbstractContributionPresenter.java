package com.onpositive.businessdroids.ui.actions.presenters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;


public abstract class AbstractContributionPresenter implements
		IContributionPresenter {

	public AbstractContributionPresenter() {
		super();
	}

	@Override
	public void presentContributionItem(IContributionItem item,
			IViewer dataView, View anchor) {
		if (item instanceof ICompositeContributionItem) {
			List<IContributionItem> enabled = new ArrayList<IContributionItem>();
			IContributionItem[] items = ((ICompositeContributionItem) item)
					.getChildren();
			for (IContributionItem item2 : items) {
				if (item2.isEnabled()) {
					enabled.add(item2);
				}
			}
			if (enabled.size() == 0) {
				return;
			}
			if (enabled.size() == 1) {
				this.processSingle(enabled.get(0), anchor, dataView);
				return;
			}
			this.doPresent(item, dataView,
					enabled.toArray(new IContributionItem[enabled.size()]),
					anchor);
		}

	}

	protected abstract void doPresent(IContributionItem item,
			IViewer dataView, IContributionItem[] enabled,
			View anchor);

	@Override
	public void processSingle(IContributionItem item, View anchor,
			IViewer dataView) {
		if (item instanceof ActionContribution) {
			((ActionContribution) item).onRun();
		} else if (item instanceof ICompositeContributionItem) {
			dataView.getCurrentTheme().getContributionPresenter(2,(ICompositeContributionItem) item)
					.presentContributionItem(item, dataView, anchor);
		}
	}

}