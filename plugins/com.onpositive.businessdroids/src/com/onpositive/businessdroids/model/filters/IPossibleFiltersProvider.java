package com.onpositive.businessdroids.model.filters;

import com.onpositive.businessdroids.model.TableModel;

public interface IPossibleFiltersProvider {

	IFilter[] getPossibleFilters(TableModel tableModel);

}
