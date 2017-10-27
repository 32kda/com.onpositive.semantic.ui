package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasImage;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class RemoveFiltersListActionContribution extends
		AbstractCompositeContributionItem implements
		ICompositeContributionItem, IHasImage {

	protected final StructuredDataView dataView;

	public RemoveFiltersListActionContribution(String text, Drawable icon,
			StructuredDataView dataView) {
		super(text, icon);
		this.dataView = dataView;
	}

	@Override
	public String getId() {
		return "Filters remove";
	}

	@Override
	public boolean isEnabled() {
		return this.dataView.getTableModel().getRegisteredFilters().length > 0;
	}

	@Override
	public IContributionItem[] getChildren() {
		IFilter[] registeredFilters = this.dataView.getTableModel()
				.getRegisteredFilters();
		int length = registeredFilters.length;
		if (length > 1) {
			length += 1;
		}
		IContributionItem[] result = new IContributionItem[length];
		for (int i = 0; i < registeredFilters.length; i++) {
			result[i] = new RemoveFilterActionContribution(
					registeredFilters[i], this.dataView);
		}
		if (length > 1) {
			result[length - 1] = new RemoveAllFiltersActionContribution(
					registeredFilters, this.dataView);
		}
		return result;
	}

}
