package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.Date;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.BooleanFilter;
import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.model.filters.DateFilter;
import com.onpositive.businessdroids.model.filters.ExplicitValueFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IPossibleFiltersProvider;


public class BasicPossibleFiltersProvider implements IPossibleFiltersProvider {

	protected final IColumn column;
	protected IFilter[] cache;

	public BasicPossibleFiltersProvider(IColumn column) {
		this.column = column;
	}

	@Override
	public IFilter[] getPossibleFilters(TableModel tableModel) {
		if (this.cache != null) {
			return this.cache;
		}
		IFilter[] currentFilters = tableModel
				.getFieldFilters(this.column);
		IFilter filter;
		IFilter[] result = new IFilter[0];
		Class<?> type = this.column.getType();
		if (type == null)
			return new IFilter[0];
		if (Number.class.isAssignableFrom(type)) {
			filter = this.findExistingFilter(currentFilters,
					ComparableFilter.class);
			if (filter == null) {
				filter = new ComparableFilter(tableModel, this.column, null,
						null);
			}
			result = new IFilter[] { filter };
		}
		if (String.class.isAssignableFrom(type)) {
			filter = this.findExistingFilter(currentFilters,
					ExplicitValueFilter.class);
			if (filter == null) {
				filter = new ExplicitValueFilter(tableModel, this.column, null);
			}
			result = new IFilter[] { filter };
		}
		if (Boolean.class.equals(type)) {
			filter = this.findExistingFilter(currentFilters,
					BooleanFilter.class);
			if (filter == null) {
				filter = new BooleanFilter(tableModel, this.column, true);
			}
			result = new IFilter[] { new BooleanFilter(tableModel, this.column,
					true) };
		}
		// FIXME
		if (Date.class.equals(type)) {
			filter = this.findExistingFilter(currentFilters, DateFilter.class);
			if (filter == null) {
				filter = new DateFilter(tableModel, this.column, null, null);
			}
			result = new IFilter[] { new DateFilter(tableModel, this.column,
					null, null) };
		}
		this.cache = result;
		return result;
	}

	protected IFilter findExistingFilter(IFilter[] currentFilters,
			Class<?> class1) {
		for (IFilter filter : currentFilters) {
			if (filter.getClass().equals(class1)) {
				return filter;
			}
		}
		return null;
	}

}
