package com.onpositive.businessdroids.model.filters;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;

public class BooleanFilter extends AbstractColumnFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -572574981199839608L;
	boolean value = true;

	public BooleanFilter(TableModel tableModel, IColumn column, boolean value) {
		super(tableModel, column);
		this.value = value;
	}

	@Override
	public String getTitle() {
		return "Value";
	}

	@Override
	protected boolean valueMatches(Object filterPropValue) {
		if (filterPropValue instanceof Boolean) {
			return ((Boolean) filterPropValue).booleanValue() == this.value;
		}
		return true;
	}

	public boolean isValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public void loadSpecificData(IStore store) {
		this.value = store.getBoolean("value", true);
	}

	@Override
	public void saveSpecificData(IStore store) {
		store.putBoolean("value", this.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (this.value ? 1231 : 1237);
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
		BooleanFilter other = (BooleanFilter) obj;
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
