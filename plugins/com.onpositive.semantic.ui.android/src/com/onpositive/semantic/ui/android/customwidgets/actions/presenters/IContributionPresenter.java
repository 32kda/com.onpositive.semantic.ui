package com.onpositive.semantic.ui.android.customwidgets.actions.presenters;

import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;

import android.view.View;

/**
 * Common interface for {@link IContributionItem} -> {@link View}/other UI widget converters
 * @author 32kda
 */
public interface IContributionPresenter {
	
	public abstract void presentContributionItem(IContributionItem item,
			View anchor);
	
	public abstract void presentObjectContributionItem(IObjectContributionManager manager, IStructuredSelection selection,
			View anchor);

	public abstract void processSingle(IContributionItem item, View anchor);
}
