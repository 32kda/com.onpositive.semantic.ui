package com.onpositive.businessdroids.ui.dataview;

import java.util.List;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;

public class GroupsProviderQ extends AbstractGroupsProvider{
private TableModel tableModel;
private List<Group> groups;

//	protected List<Group> groups = new ArrayList<Group>();
	
	
	protected void doGroupSort(TableModel tm) {
		IField sortField = tm.getGroupSortField();
		if (sortField != null) {
//			boolean ascendingSort = tm.isAscendingSort();
//			try{
//			Comparator comparator = AbstractCollectionBasedModel.getComparator(sortField, ascendingSort);
//			Collections.sort(this.groups, comparator);
//			}catch (Exception e) {
//				Comparator comparator = new BasicFieldComparator(sortField, ascendingSort);
//				try{
//				Collections.sort(this.groups, comparator);
//				}catch (Exception e1) {
//
//				}
//			}
		}
	}

	protected void reinit(IGroupingCalculator currentGroupingCalculator,
			TableModel tableModel) {
		this.tableModel = tableModel;
		if (currentGroupingCalculator == null) {
			return;
		}
//		this.groupColumn = tm2.getCurrentGroupField();
//		TableModel tableModel = this.dataView.getTableModel();
		IField groupField = null;
		if (currentGroupingCalculator instanceof IFieldGroupingCalculator) {
			groupField = ((IFieldGroupingCalculator) currentGroupingCalculator).getGroupField();
		}
		//this.groups = currentGroupingCalculator.calculateGroups(tableModel);
		this.doGroupSort(tableModel);
	}

	public int size() {
		return tableModel.getItemCount();
	}

	public Object getGroup(int groupPosition) {
		return tableModel.getItem(groupPosition);
	}
}
