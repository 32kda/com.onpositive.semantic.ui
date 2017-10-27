package com.onpositive.businessdroids.ui.dataview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.impl.AbstractCollectionBasedModel;
import com.onpositive.businessdroids.model.impl.BasicFieldComparator;

public class GroupsProvider extends AbstractGroupsProvider{
	protected List<Group> groups = new ArrayList<Group>();
	
	
	protected void doGroupSort(TableModel tm) {
		IField sortField = tm.getGroupSortField();
		if (sortField != null) {
			boolean ascendingSort = tm.isAscendingSort();
			try{
			Comparator comparator = AbstractCollectionBasedModel.getComparator(sortField, ascendingSort);
			Collections.sort(this.groups, comparator);
			}catch (Exception e) {
				Comparator comparator = new BasicFieldComparator(sortField, ascendingSort);
				try{
				Collections.sort(this.groups, comparator);
				}catch (Exception e1) {

				}
			}
		}
	}

	protected void reinit(IGroupingCalculator currentGroupingCalculator,
			TableModel tableModel) {
		if (currentGroupingCalculator == null) {
			return;
		}
//		this.groupColumn = tm2.getCurrentGroupField();
//		TableModel tableModel = this.dataView.getTableModel();
		IField groupField = null;
		if (currentGroupingCalculator instanceof IFieldGroupingCalculator) {
			groupField = ((IFieldGroupingCalculator) currentGroupingCalculator).getGroupField();
		}
		this.groups = currentGroupingCalculator.calculateGroups(tableModel);
		this.doGroupSort(tableModel);
	}

	public int size() {
		return groups.size();
	}

	public Group getGroup(int groupPosition) {
		Group group = groups.get(groupPosition);
		if (groupPosition==0){
			System.out.println("A");
		}
		return group;
	}
}
