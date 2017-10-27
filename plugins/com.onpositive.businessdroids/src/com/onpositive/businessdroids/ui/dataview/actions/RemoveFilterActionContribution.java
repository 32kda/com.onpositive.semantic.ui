package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class RemoveFilterActionContribution extends ActionContribution {

	protected final StructuredDataView dataView;
	protected final IFilter filter;

	public RemoveFilterActionContribution(IFilter filter,
			StructuredDataView dataView) {
		super("", dataView.getCurrentTheme().getIconProvider()
				.getRemoveFilterIcon(dataView.getContext()));
		this.filter = filter;
		this.dataView = dataView;
	}

	protected Drawable getIconDrawable() {
		return this.dataView.getCurrentTheme().getIconProvider()
				.getRemoveFilterIcon(this.dataView.getContext());
	}

	@Override
	public boolean isEnabled() {
		if (this.dataView.getTableModel().hasFilter(this.filter)) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		this.dataView.getTableModel().removeFilter(this.filter);
	}

	@Override
	public String getText() {
		if (this.filter instanceof AbstractColumnFilter) {
			return ((AbstractColumnFilter) this.filter).getColumn().getId()
					+ " (" + this.filter.getTitle() + ")";
		}
		return this.filter.getTitle();
	}
}
