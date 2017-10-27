package com.onpositive.semantic.ui.android.customwidgets.actions.presenters;

import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.ui.android.customwidgets.dialogs.ContributionItemDialog;

import android.content.Context;
import android.view.View;


public class DialogContributionPresenter extends AbstractContributionPresenter
		implements IContributionPresenter {

	private Context context;

	public DialogContributionPresenter(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void doPresent(IContributionItem item,
			 IContributionItem[] enabled,
			View anchor) {
		ContributionItemDialog dialog = new ContributionItemDialog(
				getContext(), enabled, ((IAction) item).getText());
		dialog.show();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
