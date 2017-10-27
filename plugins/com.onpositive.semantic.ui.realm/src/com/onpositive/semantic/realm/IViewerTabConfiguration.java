package com.onpositive.semantic.realm;

import java.util.List;

public interface IViewerTabConfiguration extends INamedEntity{

	List<IColumnConfiguration> getColumns();
	
	List<IFilterConfiguration> getFilters();
}
