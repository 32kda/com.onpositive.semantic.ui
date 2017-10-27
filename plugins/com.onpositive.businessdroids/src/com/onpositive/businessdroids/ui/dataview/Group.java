package com.onpositive.businessdroids.ui.dataview;

import java.util.HashMap;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;


public class Group {

	protected Object key;
	protected IColumn column;
	protected Object[] values;

	protected HashMap<IColumn, Object> props = new HashMap<IColumn, Object>();
	private TableModel mdl;

	public Group(TableModel mdl, IField fld, Object key, Object[] groupedItems) {
		this.key = key;
		this.values = groupedItems;
		this.column = (IColumn) fld;
		this.mdl=mdl;
	}

	
	public Object getPropertyValue(IColumn column) {
		if (column.isCaption()) {
			return this.key;
		}
		if (this.column.getId().equals(column.getId())) {
			return null;
		}
		if (this.props.containsKey(column)) {
			return this.props.get(column);
		}
		
		IAggregator aggregatorUsed = column.getAggregator();
		Object aggregatedValue = mdl.getAggregatedValue(aggregatorUsed, values,column);
		this.props.put(column, aggregatedValue);
		return aggregatedValue;
	}

	public Object getKey() {
		return this.key;
	}

	public IColumn getColumn() {
		return this.column;
	}

	public void clearField(IColumn column) {
		this.props.remove(column);
	}


	public void setKey(Object date) {
		this.key=date;
	}


	public int getChildrenCount() {
		return values.length;
	}


	public Object getChild(int position) {		
		return values[position];
	}
}
