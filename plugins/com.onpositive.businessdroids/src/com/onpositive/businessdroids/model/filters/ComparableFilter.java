package com.onpositive.businessdroids.model.filters;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.types.ComparableRange;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;

public class ComparableFilter extends AbstractColumnFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	protected Comparable min = null;
	@SuppressWarnings("rawtypes")
	protected Comparable max = null;

	@SuppressWarnings("rawtypes")
	public ComparableFilter(TableModel tableModel, IColumn column,
			Comparable min, Comparable max) {
		super(tableModel, column);
		this.min = min;
		this.max = max;
	}

	@Override
	public String getTitle() {
		return "Compare";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean valueMatches(Object filterPropValue) {
		if ((this.min != null) && (filterPropValue == null)) {
			return false;
		}
		if (!(filterPropValue instanceof Comparable)) {
			return true;
		}
		if ((this.min != null) && (this.min.compareTo(filterPropValue) > 0)) {
			return false;
		}
		if ((this.max != null) && (this.max.compareTo(filterPropValue) < 0)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public Comparable getMin() {
		return this.min;
	}

	@SuppressWarnings("rawtypes")
	public void setMin(Comparable min) {
		this.min = min;
	}

	@SuppressWarnings("rawtypes")
	public Comparable getMax() {
		return this.max;
	}

	@SuppressWarnings("rawtypes")
	public void setMax(Comparable max) {
		this.max = max;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void loadSpecificData(IStore store) {
		this.min = (Comparable) store.loadObject("min");
		this.max = (Comparable) store.loadObject("max");
	}

	@Override
	public void saveSpecificData(IStore store) {
		store.storeObject("min", this.min);
		store.storeObject("max", this.max);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.max == null) ? 0 : this.max.hashCode());
		result = prime * result
				+ ((this.min == null) ? 0 : this.min.hashCode());
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
		ComparableFilter other = (ComparableFilter) obj;
		if (this.max == null) {
			if (other.max != null) {
				return false;
			}
		} else if (!this.max.equals(other.max)) {
			return false;
		}
		if (this.min == null) {
			if (other.min != null) {
				return false;
			}
		} else if (!this.min.equals(other.min)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValue() {
		return new ComparableRange(min,max);
	}

}
