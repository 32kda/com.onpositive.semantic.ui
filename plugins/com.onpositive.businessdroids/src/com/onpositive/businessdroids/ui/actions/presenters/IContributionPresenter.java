package com.onpositive.businessdroids.ui.actions.presenters;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.IContributionItem;

import android.view.View;


public interface IContributionPresenter {
	public void presentContributionItem(IContributionItem item,
			IViewer dataView, View anchor);

	public abstract void processSingle(IContributionItem item, View anchor,
			IViewer dataView);
}
