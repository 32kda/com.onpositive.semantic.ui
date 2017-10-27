package com.onpositive.businessdroids.ui.dataview.handlers;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasImage;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;


public class QuickActionHeaderClickHandler extends
		AbstractCompositeClickHandler implements ITablePartClickHandler {

	// protected HashMap<Field, IContributionItemProvider>
	// contributionItemProviders = new HashMap<Field,
	// IContributionItemProvider>();
	protected QuickActionBar actionBar;

	public QuickActionHeaderClickHandler(StructuredDataView dataView) {
		super(dataView);
	}

	@Override
	public void handleClick(IColumn column, View source) {
		if (this.actionBar == null) {
			this.actionBar = new QuickActionBar(source.getContext());
		}
		if (this.prepareActions(column)) {
			this.actionBar.show(source);
		}
	}

	protected boolean prepareActions(final IColumn column) {
		Collection<IContributionItem> contributionItems = this
				.getEnabledContributions(column);
		if (contributionItems.size() == 0) {
			return false;
		}
		this.actionBar.clearAllQuickActions();
		List<IContributionItem> resultItems = new ArrayList<IContributionItem>();
		for (IContributionItem contributionItem : contributionItems) {
			QuickAction quickAction = this.createQuickAction(contributionItem);
			if (quickAction != null) {
				this.actionBar.addQuickAction(quickAction);
				resultItems.add(contributionItem);
			} else {
				throw new AssertionError(
						"Can't create QuickAction for contribution "
								+ contributionItem);
			}
		}
		final IContributionItem[] itemsArray = resultItems
				.toArray(new IContributionItem[0]);
		this.actionBar
				.setOnQuickActionClickListener(new OnQuickActionClickListener() {

					@Override
					public void onQuickActionClicked(QuickActionWidget widget,
							int position) {
						IContributionItem contributionItem = itemsArray[position];
						if (contributionItem instanceof ActionContribution) {
							((ActionContribution) contributionItem).onRun();
						} else if (contributionItem instanceof ICompositeContributionItem) {
							QuickActionHeaderClickHandler.this.dataView
									.getCurrentTheme()
									.getContributionPresenter(2, (ICompositeContributionItem) contributionItem)
									.presentContributionItem(
											contributionItem,
											QuickActionHeaderClickHandler.this.dataView,
											QuickActionHeaderClickHandler.this.actionBar
													.getContentView());
						}
					}
				});
		return true;
	}

	protected QuickAction createQuickAction(IContributionItem contributionItem) {
		if (contributionItem instanceof IHasImage) {
			ITheme currentTheme = this.dataView.getCurrentTheme();
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
				icon = new StateListDrawable() {
					@Override
					protected boolean onStateChange(int[] stateSet) {
						System.out.println("onStateChange() "
								+ Arrays.toString(stateSet));
						return super.onStateChange(stateSet);
					}
				};
				((StateListDrawable) icon).addState(new int[] {
						-android.R.attr.state_pressed,
						-android.R.attr.state_selected,
						-android.R.attr.state_focused }, drawable1);
				((StateListDrawable) icon).addState(
						new int[] { android.R.attr.state_pressed }, drawable2);
				text = "";
			}
			if (text == null) {
				text = "";
			}
			QuickAction action = new QuickAction(icon, text);
			return action;
		}
		return null;
	}

}
