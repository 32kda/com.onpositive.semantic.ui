package com.onpositive.businessdroids.model.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;


public class ExplicitValueFilter extends AbstractColumnFilter {

	private static final String COUNT = "count";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Collection<Object> values;

	public ExplicitValueFilter(TableModel tableModel, IColumn column,
			List<Object> values) {
		super(tableModel, column);
		this.values = values;
	}

	@Override
	public String getTitle() {
		return "Values";
	}

	@Override
	protected boolean valueMatches(Object filterPropValue) {
		if (this.values == null) {
			return true;
		}
		if (filterPropValue instanceof Object[]){
			Object[]c=(Object[]) filterPropValue;
			for (Object o:c){
				if (this.values.contains(o)){
					return true;
				}
			}
			return false;
		}
		if (filterPropValue instanceof Collection){
			Collection c=(Collection) filterPropValue;
			for (Object o:c){
				if (this.values.contains(o)){
					return true;
				}
			}
			return false;
		}
		return this.values.contains(filterPropValue);
	}

	public Collection<Object> getValues() {
		return this.values;
	}

	public void setValue(Object value) {
		this.values.clear();
		this.values.add(value);
	}

	public void addValue(Object value) {
		this.values.add(value);
	}

	public void setValues(Collection<Object> result) {
		this.values = result;
	}

	@Override
	public void loadSpecificData(IStore store) {
		this.values = new ArrayList<Object>();
		int int1 = store.getInt(ExplicitValueFilter.COUNT, 0);
		for (int a = 0; a < int1; a++) {
			Object loadObject = store.loadObject(a + "");
			if (loadObject != null) {
				this.values.add(loadObject);
			}
		}
	}

	@Override
	public void saveSpecificData(IStore store) {
		store.putInt(ExplicitValueFilter.COUNT, this.values.size());
		int a = 0;
		for (Object o : this.values) {
			store.storeObject("" + a, o);
			a++;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.values == null) ? 0 : this.values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ExplicitValueFilter other = (ExplicitValueFilter) obj;
		if (this.values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!this.values.equals(other.values)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValue() {
		return values;
	}

}
