package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

public class RemoveAllFiltersActionContribution extends ActionContribution {

	protected final StructuredDataView dataView;
	protected final IFilter[] registeredFilters;

	public RemoveAllFiltersActionContribution(IFilter[] registeredFilters,
			StructuredDataView dataView) {
		super("Remove all filters", null);
		this.registeredFilters = registeredFilters;
		this.dataView = dataView;
	}

	@Override
	public String getId() {
		return "Filters remove all";
	}

	@Override
	public boolean isEnabled() {
		return this.registeredFilters.length > 0;
	}

	@Override
	public void run() {
		for (IFilter filter : this.registeredFilters) {
			this.dataView.getTableModel().removeFilter(filter);
		}
	}

}
