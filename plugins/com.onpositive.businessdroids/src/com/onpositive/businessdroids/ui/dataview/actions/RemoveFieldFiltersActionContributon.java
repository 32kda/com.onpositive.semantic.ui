package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class RemoveFieldFiltersActionContributon extends
		ColumnActionContribution {

	public RemoveFieldFiltersActionContributon(IColumn column,
			StructuredDataView dataView, Drawable icon) {
		super("Remove filters on " + column.getId(), icon, column, dataView);
	}

	protected Drawable getIconDrawable() {
		return this.dataView.getCurrentTheme().getIconProvider()
				.getRemoveFilterIcon(this.dataView.getContext());
	}

	@Override
	public String getId() {
		return "Filter remove";
	}

	@Override
	public boolean isEnabled() {
		if (this.dataView.getTableModel().getFieldFilters(this.column).length > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		IFilter[] fieldFilters = this.dataView
				.getColumnFilters(this.column);
		for (IFilter filter : fieldFilters) {
			this.dataView.getTableModel().removeFilter(filter);
		}
	}

}
