package com.onpositive.businessdroids.ui.dataview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.onpositive.businessdroids.model.TableModel;

public class GroupingDataAdapter extends BaseExpandableListAdapter implements
		ExpandableListAdapter {

	protected StructuredDataView dataView;

	protected TableModelDataAdapter adapter;

	protected AbstractGroupsProvider groupsProvider;

	
	protected void onModelChanged(TableModel model){
		if (model.getCurrentGroupingCalculator() != null) {
			groupsProvider.reinit(
					model.getCurrentGroupingCalculator(), model);
		} 
		GroupingDataAdapter.this.notifyDataSetChanged();
	}

	public GroupingDataAdapter(final StructuredDataView dataView,
			TableModelDataAdapter dt) {
		super();
		this.dataView = dataView;
		if (dataView.getTableModel() instanceof IGroupAwareTableModel){
			groupsProvider=new GroupsProviderQ();
		}
		else{
			groupsProvider=new GroupsProvider();
		}
		this.adapter = dt;
		TableModel tableModel = dataView.getTableModel();		
		if (tableModel.getCurrentGroupingCalculator() != null) {
			groupsProvider.reinit(tableModel.getCurrentGroupingCalculator(), tableModel);
		} 
	}

//	private void reinit(IField fl, TableModel tm) {
//		this.groupColumn = fl;
//		if (fl == null) {
//			return;
//		}
//		this.basicGroup(tm);
//		this.doGroupSort(tm);
//
//	}

//	@SuppressWarnings("rawtypes")
//	protected void basicGroup(TableModel tm) {
//		ArrayList<Group> groupsm = actualBasicGroup(tm,this.groupColumn);
//		this.groups=groupsm;
//	}

	@Override
	public int getGroupCount() {
		return groupsProvider.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Group group = (Group) this.getGroup(groupPosition);
		return group.getChildrenCount();
	}

	@Override
	public boolean areAllItemsEnabled() {

		return true;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupsProvider.getGroup(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Group group = (Group) this.getGroup(groupPosition);
		return group.getChild(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		int flatPosition = groupPosition;
		if (parent instanceof ExpandableListView) {
			long packedPosition = ExpandableListView
					.getPackedPositionForGroup(groupPosition);
			flatPosition = ((ExpandableListView) parent)
					.getFlatListPosition(packedPosition);
		}
		View view = this.adapter.getView(flatPosition, null, parent,
				this.getGroup(groupPosition));
		view.setEnabled(false);
		view.setFocusableInTouchMode(false);
		view.setFocusable(false);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		int flatPosition = childPosition;
		if (parent instanceof ExpandableListView) {
			long packedPosition = ExpandableListView.getPackedPositionForChild(
					groupPosition, childPosition);
			flatPosition = ((ExpandableListView) parent)
					.getFlatListPosition(packedPosition);
		}
		return this.adapter.getView(flatPosition, convertView, parent,
				this.getChild(groupPosition, childPosition));
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public TableModelDataAdapter getChildAdapter() {
		return this.adapter;
	}

}
