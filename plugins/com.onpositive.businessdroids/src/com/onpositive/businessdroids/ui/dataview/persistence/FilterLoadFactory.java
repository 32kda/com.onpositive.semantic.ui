package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.BasicStringFilter;
import com.onpositive.businessdroids.model.filters.BooleanFilter;
import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.model.filters.DateFilter;
import com.onpositive.businessdroids.model.filters.ExplicitValueFilter;
import com.onpositive.businessdroids.model.filters.IFilter;

public class FilterLoadFactory {

	public static IFilter createFilter(String className, IStore store,
			TableModel tableModel) {
		if (className != null) {
			try {
				IFilter flt = FilterLoadFactory.createFilter(className,
						tableModel);

				return flt;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	// FIXME not extensible
	protected static IFilter createFilter(String className,
			TableModel tableModel) {
		if (className.equals(BooleanFilter.class.getName())) {
			return new BooleanFilter(tableModel, null, false);
		}
		if (className.equals(ComparableFilter.class.getName())) {
			return new ComparableFilter(tableModel, null, null, null);
		}
		if (className.equals(DateFilter.class.getName())) {
			return new DateFilter(tableModel, null, null, null);
		}
		if (className.equals(ExplicitValueFilter.class.getName())) {
			return new ExplicitValueFilter(tableModel, null, null);
		}
		if (className.equals(BasicStringFilter.class.getName())) {
			return new BasicStringFilter(null, "", 0);
		}
		return null;
	}

}
