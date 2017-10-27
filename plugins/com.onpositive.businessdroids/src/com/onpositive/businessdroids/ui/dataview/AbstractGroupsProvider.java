package com.onpositive.businessdroids.ui.dataview;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;

public abstract class AbstractGroupsProvider {

	public AbstractGroupsProvider() {
		super();
	}

	protected abstract void doGroupSort(TableModel tm) ;

	protected abstract void reinit(IGroupingCalculator currentGroupingCalculator, TableModel tableModel) ;

	public abstract int size(); 

	public abstract Object getGroup(int groupPosition);

}