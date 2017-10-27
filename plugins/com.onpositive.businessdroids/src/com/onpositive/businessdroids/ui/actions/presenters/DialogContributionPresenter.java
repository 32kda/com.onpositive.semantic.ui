package com.onpositive.businessdroids.ui.actions.presenters;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dialogs.ContributionItemDialog;

import android.view.View;


public class DialogContributionPresenter extends AbstractContributionPresenter
		implements IContributionPresenter {

	@Override
	protected void doPresent(IContributionItem item,
			IViewer dataView, IContributionItem[] enabled,
			View anchor) {
		ContributionItemDialog dialog = new ContributionItemDialog(
				dataView.getContext(), enabled, item.getText(), dataView);
		dialog.show();
	}

}
