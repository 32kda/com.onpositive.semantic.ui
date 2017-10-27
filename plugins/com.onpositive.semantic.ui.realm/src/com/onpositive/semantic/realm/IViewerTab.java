package com.onpositive.semantic.realm;

import java.util.Set;

public interface IViewerTab extends INamedEntity{

	Set<IColumnDefinition> requiredColumns();
	
	IColumnDefinition firstColumn();
}
