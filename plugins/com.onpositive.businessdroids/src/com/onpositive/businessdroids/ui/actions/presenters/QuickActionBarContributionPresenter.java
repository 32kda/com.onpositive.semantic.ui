package com.onpositive.businessdroids.ui.actions.presenters;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasImage;
import com.onpositive.businessdroids.ui.themes.ITheme;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

public class QuickActionBarContributionPresenter extends
		AbstractContributionPresenter {

	private static final int NEXT_LEVEL = 2;

	protected QuickAction createQuickAction(IContributionItem contributionItem,
			IViewer dataView) {
		if (contributionItem instanceof IHasImage) {
			ITheme currentTheme = dataView.getCurrentTheme();
			Drawable icon = ((IHasImage) contributionItem).getIcon();
			String text = contributionItem.getText();
			if (icon == null) {
				icon = currentTheme.getQuickActionBackgroundDrawable();
			} else {
				LayerDrawable drawable1 = new LayerDrawable(new Drawable[] {
						currentTheme.getQuickActionBackgroundDrawable(), icon });
				LayerDrawable drawable2 = new LayerDrawable(
						new Drawable[] {
								currentTheme
										.getSelectedQuickActionBackgroundDrawable(),
								icon });
				icon = new StateListDrawable();
				((StateListDrawable) icon).addState(new int[] {
						-android.R.attr.state_pressed,
						-android.R.attr.state_selected,
						-android.R.attr.state_focused }, drawable1);
				((StateListDrawable) icon).addState(
						new int[] { android.R.attr.state_pressed }, drawable2);
				((StateListDrawable) icon).addState(
						new int[] { android.R.attr.state_selected }, drawable2);
			}
			if ((text == null) || (icon != null)) {
				text = "";
			}
			QuickAction action = new QuickAction(icon, text);
			return action;
		}
		return null;
	}

	@Override
	protected void doPresent(IContributionItem item, final IViewer dataView,
			final IContributionItem[] enabled, View anchor) {
		final QuickActionBar actionBar = new QuickActionBar(
				dataView.getContext());
		for (IContributionItem childItem : enabled) {

			QuickAction quickAction = this.createQuickAction(childItem,
					dataView);
			actionBar.addQuickAction(quickAction);
		}
		actionBar
				.setOnQuickActionClickListener(new OnQuickActionClickListener() {

					@Override
					public void onQuickActionClicked(QuickActionWidget widget,
							int position) {
						if (enabled[position] instanceof ActionContribution) {
							((ActionContribution) enabled[position]).onRun();
						} else if (enabled[position] instanceof ICompositeContributionItem) {
							dataView.getCurrentTheme()
									.getContributionPresenter(
											QuickActionBarContributionPresenter.NEXT_LEVEL, (ICompositeContributionItem) enabled[position])
									.presentContributionItem(enabled[position],
											dataView,
											actionBar.getContentView());
						}
					}
				});
		actionBar.show(anchor);

	}

}
