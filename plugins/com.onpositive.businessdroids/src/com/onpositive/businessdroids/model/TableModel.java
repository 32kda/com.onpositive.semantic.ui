package com.onpositive.businessdroids.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IAggregatorChangeListener;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.BasicStringFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.impl.BasicArray;
import com.onpositive.businessdroids.model.impl.BasicFieldComparator;
import com.onpositive.businessdroids.model.impl.FieldValueArray;

public abstract class TableModel implements IArray {

	protected List<IModelChangeListener> listeners = new ArrayList<IModelChangeListener>();
	protected IField sortField;

	protected IField groupSortField;

	protected boolean isAscendingSort;
	protected IColumn[] columns;
	protected List<IFilter> filters = new ArrayList<IFilter>();
	protected boolean hasFilters;
	Object selectedRecord ;

	private IAggregatorChangeListener aListener = new IAggregatorChangeListener() {
	
		@Override
		public void aggregatorChanged(IAggregator oldAggregator,
				IAggregator newAggregator, IColumn column) {
			
			onAggregatorChanged(oldAggregator, newAggregator, column);
		}
	};

	public static Comparator<?> getComparator(IField field, boolean ascending) {
		Comparator<?> comparator = null;
		if (field instanceof IColumn) {
			IColumn c = (IColumn) field;
			comparator = c.getComparator(ascending);
		}
		if (comparator == null) {
			comparator = new BasicFieldComparator(field, ascending);
		}
		return comparator;
	}

	public IField getGroupSortField() {
		if (groupSortField != null) {
			return groupSortField;
		}
		return sortField;
	}

	public void setGroupSortField(IField groupSortField) {
		this.groupSortField = groupSortField;
		fireModelChanged();
	}

	public IFilter[] getRegisteredFilters() {
		return this.filters.toArray(new IFilter[0]);
	}

	@Override
	public Class<?> getComponentType() {
		return Object.class;
	}

	public final void addFilter(IFilter filter) {
		if (!this.filters.contains(filter)) {
			this.filters.add(filter);
		}
		onFiltersChanged(filters);
		this.hasFilters = true;
		this.fireModelChanged();
	}

	protected abstract void onFiltersChanged(List<IFilter> newFilters);

	public final void removeFilter(IFilter filter) {
		this.filters.remove(filter);
		this.hasFilters = this.filters.size() > 0;
		onFiltersChanged(filters);
		this.fireModelChanged();
	}

	public final void replaceFilter(IFilter oldFilter, IFilter newFilter) {
		int idx = this.filters.indexOf(oldFilter);
		this.filters.remove(oldFilter);
		this.filters.add(idx, newFilter);
		this.hasFilters = true;
		onFiltersChanged(filters);
		this.fireModelChanged();
	}

	public IFilter[] getFieldFilters(IField field) {
		List<IFilter> result = new ArrayList<IFilter>();
		for (IFilter iFilter : this.filters) {
			IFilter filter = iFilter;
			if (filter instanceof AbstractColumnFilter) {
				IColumn column = ((AbstractColumnFilter) filter).getColumn();
				if (column!=null&&column.equals(field)) {
					result.add((AbstractColumnFilter) filter);
				}
			}
			if (filter instanceof BasicStringFilter){
				BasicStringFilter s=(BasicStringFilter) filter;
				if (s.getProperty()!=null&&s.getProperty().equals(field.getId())){
					result.add(s);					
				}
			}
		}
		return result.toArray(new IFilter[result.size()]);
	}

	public IColumn[] getColumns() {
		return this.columns;
	}

	public void setColumns(IColumn[] columns) {
		if (this.columns != null) {
			for (IColumn c : this.columns) {
				c.removeAggregatorChangeListener(aListener);
			}
		}
		if (columns != null) {
			for (IColumn c : columns) {
				c.addAggregatorChangeListener(aListener);
			}
		}
		this.columns = columns;
		fireModelChanged();
	}

	public void dispose() {
		setColumns(null);
	}

	public IColumn getColumnById(String id) {
		for (IColumn column : this.columns) {
			if (column.getId().equalsIgnoreCase(id)) {
				return column;
			}
		}
		return null;
	}


	protected IGroupingCalculator currentGroupingCalculator;

	protected boolean eventsEnabled = true;
	protected boolean delayedEvent = false;

	public abstract Long getUnfilteredItemCount();

	public abstract int getItemCount();

	public abstract Object getItem(int i);

	// TODO FIX ME
	public Object[] getUniqueValuesForColumnAsArray(IField field) {
		LinkedHashSet<Object> result = new LinkedHashSet<Object>();
		for (Object t : this) {
			Object value = field.getPropertyValue(t);
			if (value instanceof Collection){
				Collection<?> c=(Collection<?>) value;
				result.addAll(c);
			}
			else if (value instanceof Object[]){
				Object[] m=(Object[]) value;
				for (Object e:m){
					result.add(e);
				}
			}
			else{
			result.add(value);
			}
		}
		return result.toArray();
	}

	public IField getSortField() {
		return this.sortField;
	}

	public boolean isAscendingSort() {
		return this.isAscendingSort
				&& (this.sortField != null || this.groupSortField != null);
	}

	public void setAscendingSort(boolean as) {
		if (this.isAscendingSort != as) {
			this.isAscendingSort = as;
			fireModelChanged();
		}
	}

	public TableModel(IColumn[] columns) {
		setColumns(columns);
	}

	/**
	 * Sorts model content
	 * 
	 * @param sortField
	 *            Field to sort with
	 * @param ascending
	 *            <code>true</code> for ascending sort, <code>false</code> for
	 *            descending
	 */
	public void sort(IField sortField, boolean ascending) {
		this.sortField = sortField;
		this.isAscendingSort = ascending;
		this.internalSort(sortField, ascending);
		this.fireModelChanged();
	}

	protected abstract void internalSort(IField sortField2, boolean ascending);

	public void addModelChangeListener(IModelChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeModelChangeListener(IModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	public void fireModelChanged() {
		if (!this.eventsEnabled) {
			this.delayedEvent = true;
			return;
		}
		for (Iterator<IModelChangeListener> iterator = new ArrayList<IModelChangeListener>(
				this.listeners).iterator(); iterator.hasNext();) {
			IModelChangeListener listener = iterator.next();
			listener.modelChanged(this);
		}
	}

//	public IField getCurrentGroupField() {
//		return this.currentGroupField;
//	}

	public IGroupingCalculator getCurrentGroupingCalculator() {
		return this.currentGroupingCalculator;
	}

	public void setCurrentGrouping(
			IGroupingCalculator currentGroupingCalculator) {
//		boolean groupFieldCh = this.currentGroupField != groupColumn;
//		this.currentGroupField = groupColumn;
		IGroupingCalculator oldGroupingCalculator = this.currentGroupingCalculator;
		this.currentGroupingCalculator = currentGroupingCalculator;
		if (currentGroupingCalculator!=null && currentGroupingCalculator instanceof IFieldGroupingCalculator){
			groupSortField=((IFieldGroupingCalculator) currentGroupingCalculator).getGroupField();
		}
		if (currentGroupingCalculator != oldGroupingCalculator) {
			this.fireModelChanged();
		}
		
	}

	public boolean hasFilter(IFilter filter) {
		return this.filters.contains(filter);
	}

	public void setFilters(IFilter[] filters) {
		this.filters = new ArrayList<IFilter>(Arrays.asList(filters));
		if (filters.length > 0) {
			this.hasFilters = true;
		} else {
			hasFilters = false;
		}
		onFiltersChanged(this.filters);

		this.fireModelChanged();
	}

	public boolean isEventsEnabled() {
		return this.eventsEnabled;
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		if (eventsEnabled && !this.eventsEnabled && this.delayedEvent) {
			this.eventsEnabled = eventsEnabled;
			this.fireModelChanged();
			this.delayedEvent = false;
		}
		this.eventsEnabled = eventsEnabled;
	}
	
	protected abstract void setAggregator(IColumn column, IAggregator aggregator);

	public Object getAggregatedValue(IAggregator aggregatorUsed,
			Object[] values, IColumn column) {
		Object aggregatedValue = aggregatorUsed
				.getAggregatedValue(new FieldValueArray(column, new BasicArray(
						values)));
		return aggregatedValue;
	}

	public Object getAggregatedValue(IAggregator agr, IField fld) {
		return agr.getAggregatedValue(new FieldValueArray(fld, this));
	}

	protected void onAggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IColumn column) {
		for (IModelChangeListener m : listeners) {
			m.aggregatorChanged(oldAggregator, newAggregator, column);
		}
	}
	
	public void update() {
		//Do nothing; Override if needed 
	}

	public Object getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(Object selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

//	public List<Group> calculateGroups(
//			IGroupingCalculator currentGroupingCalculator2) {
//		return currentGroupingCalculator2.calculateGroups(this);
//	}

}
