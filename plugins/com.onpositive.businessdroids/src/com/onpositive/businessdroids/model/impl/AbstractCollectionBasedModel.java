package com.onpositive.businessdroids.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.filters.IFilter;

public abstract class AbstractCollectionBasedModel extends TableModel {

	protected List<Object> fitems = null;
	protected HashMap<IField, LinkedHashSet<Object>> uniqueValues = new HashMap<IField, LinkedHashSet<Object>>();

	@Override
	public int getItemCount() {
		this.initFilteredData();
		return this.fitems.size();
	}

	@Override
	public Iterator<Object> iterator() {
		return getContent().iterator();
	}

	public Object[] getValuesForField(IField field) {
		Collection<Object> content = this.getContent();
		Object[] values = new Object[content.size()];
		int i = 0;
		for (Object t : content) {
			Object value = field.getPropertyValue(t);
			values[i++] = value;
		}
		return values;
	}

	@Override
	public Long getUnfilteredItemCount() {
		return (long) this.items().size();
	}

	private void initFilteredData() {
		if (this.fitems == null) {
			this.fitems = this.doFilter();
			this.internalSort(this.sortField, this.isAscendingSort);
		}
	}

	@Override
	public Object getItem(int i) {
		this.initFilteredData();
		return this.fitems.get(i);
	}

	@SuppressWarnings("rawtypes")
	protected abstract List items();

	@SuppressWarnings("unchecked")
	protected List<Object> doFilter() {
		if (!this.needFiltering()) {
			return this.items();
		}
		ArrayList<Object> result = new ArrayList<Object>();
		actuallFilter(result);
		return result;
	}

	@SuppressWarnings("unchecked")
	protected void actuallFilter(ArrayList<Object> result) {
		for (Iterator<Object> iterator = this.items().iterator(); iterator
				.hasNext();) {
			Object record = iterator.next();
			boolean matches = this.isFiltered(record);
			if (matches) {
				result.add(record);
			}
		}
	}

	protected boolean isFiltered(Object record) {
		boolean matches = true;
		if (!this.hasFilters) {
			return true;
		}
		for (IFilter iFilter : this.filters) {
			IFilter filter = iFilter;
			matches = matches && filter.matches(record);
		}
		return matches;
	}

	public AbstractCollectionBasedModel() {
		super(new IColumn[0]);
	}

	public AbstractCollectionBasedModel(IColumn[] columns) {
		super(columns);
	}

	protected final boolean needFiltering() {
		return this.hasFilters;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addAll(Collection collection) {
		this.items().addAll(collection);
		if (this.needFiltering()) {
			if (this.fitems != null) {
				for (Object o : collection) {
					if (this.isFiltered(o)) {
						this.fitems.add(o);
					}
				}
			}
		}
		this.internalSort(this.sortField, this.isAscendingSort);
		this.uniqueValues.clear();
		fireModelChanged();
	}
	
	public void clear() {
		this.items().clear();
		if (this.needFiltering() && this.fitems != null) {
			this.fitems.clear();
		}		
		this.internalSort(this.sortField, this.isAscendingSort);
		this.uniqueValues.clear();
		fireModelChanged();
	}

	public Collection<Object> getContent() {
		this.initFilteredData();
		return new ArrayList<Object>(this.fitems);
	}

	@Override
	protected void internalSort(IField field, boolean ascending) {
		this.initFilteredData();
		if (field != null) {
			
			Comparator comparator = getComparator(field, ascending);
			
			Collections.sort(this.fitems==null?this.items(): this.fitems, comparator);
		}
	}

	@Override
	protected void onFiltersChanged(List<IFilter> flts) {
		fitems = null;
	}

	@SuppressWarnings("unchecked")
	public final void replaceItem(Object item, Object newItem) {
		if (this.fitems != null) {
			this.fitems.remove(item);
			if (this.isFiltered(newItem)) {
				this.fitems.add(newItem);
			}
		}
		this.items().remove(item);
		this.items().add(newItem);
		this.internalSort(this.sortField, this.isAscendingSort);
		this.uniqueValues.clear();
		fireModelChanged();
	}

	@SuppressWarnings("unchecked")
	public final void addItem(Object item) {
		if ((this.fitems != null) && this.isFiltered(item)
				&& (this.items() != this.fitems)) {
			this.fitems.add(item);
		}
		this.items().add(item);
		this.internalSort(this.sortField, this.isAscendingSort);
		this.updateUniqueValues(item);
		fireModelChanged();
	}

	protected void updateUniqueValues(Object item) {
		for (IField iField : this.uniqueValues.keySet()) {
			IField field = iField;
			if (this.uniqueValues.get(field) != null) {
				Object propertyValue = field.getPropertyValue(item);
				this.uniqueValues.get(field).add(propertyValue);
			}
		}
		;
	}

	public final void removeItem(Object item) {
		if (this.fitems != null && (this.items() != this.fitems) ) {
			this.fitems.remove(item);
		}
		this.items().remove(item);
		this.uniqueValues.clear();
		fireModelChanged();
	}

	public Object[] getUniqueValuesForColumnAsArray(IField field) {
		// FIXME WHY IT IS NEEDED
		LinkedHashSet<Object> result = this.uniqueValues.get(field);
		if (result == null) {
			Collection<Object> content = this.getContent();
			result = new LinkedHashSet<Object>();
			for (Object t : content) {
				Object value = field.getPropertyValue(t);

				if (value instanceof Collection) {
					Collection c = (Collection) value;
					result.addAll(c);
				} else if (value instanceof Object[]) {
					Object[] m = (Object[]) value;
					for (Object e : m) {
						result.add(e);
					}
				} else {
					result.add(value);
				}
			}
			this.uniqueValues.put(field, result);
		}

		return result.toArray();
	}

	@Override
	protected void setAggregator(IColumn column, IAggregator aggregator) {
		column.setAggregator(aggregator);
	}
	
}
