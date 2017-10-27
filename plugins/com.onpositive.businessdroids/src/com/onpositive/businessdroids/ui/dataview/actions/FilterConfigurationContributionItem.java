package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.AbstractListenable;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasStatefulImage;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;

public class FilterConfigurationContributionItem extends AbstractListenable
		implements ICompositeContributionItem, IHasStatefulImage {

	protected final StructuredDataView dataView;
	protected final IFilterSetupVisualizer filterSetupVisualizer;

	public FilterConfigurationContributionItem(StructuredDataView dataView,
			IFilterSetupVisualizer filterSetupVisualizer) {
		this.dataView = dataView;
		this.filterSetupVisualizer = filterSetupVisualizer;
	}

	@Override
	public boolean isEnabled() {
		IContributionItem[] children = this.getChildren();
		for (IContributionItem contributionItem : children) {
			if (contributionItem.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getText() {
		return "Configure filters";
	}

	@Override
	public IContributionItem[] getChildren() {
		return new IContributionItem[] {
				new RemoveFiltersListActionContribution(
						"Remove filters",
						this.dataView
								.getCurrentTheme()
								.getIconProvider()
								.getRemoveFilterIcon(this.dataView.getContext()),
						this.dataView),
				new AddFiltersListActionContribution("Add filter",
						this.dataView.getCurrentTheme().getIconProvider()
								.getAddFilterIcon(this.dataView.getContext()),
						this.dataView, this.filterSetupVisualizer) };
	}

	@Override
	public Drawable getIcon() {
		return this.dataView.getCurrentTheme().getIconProvider()
				.getFilterIconWhite(this.dataView.getContext());
	}

	@Override
	public Drawable getStateIcon(IViewer dataView) {
		if (this.dataView.getTableModel().getRegisteredFilters().length > 0) {
			return dataView.getCurrentTheme().getIconProvider()
					.getFilterModifiedIcon(dataView.getContext());
		}
		return this.getIcon();
	}

	@Override
	public String getId() {
		return this.getText();
	}

}
