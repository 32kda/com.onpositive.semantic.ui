package com.onpositive.businessdroids.model.filters;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;

public abstract class AbstractColumnFilter implements IValueFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected TableModel tableModel;
	protected IColumn column;

	public AbstractColumnFilter(TableModel tableModel, IColumn column) {
		super();
		this.tableModel = tableModel;
		this.column = column;
	}

	protected Object getFilterPropValue(Object record) {
		return this.column.getPropertyValue(record);
	}

	@Override
	public boolean matches(Object record) {
		return this.valueMatches(this.getFilterPropValue(record));
	}

	protected abstract boolean valueMatches(Object filterPropValue);

	public TableModel getTableModel() {
		return this.tableModel;
	}

	public IColumn getColumn() {
		return this.column;
	}

	@Override
	public final void save(IStore store) {
		store.putString("fieldId", this.column.getId());
		this.saveSpecificData(store);
	}

	public abstract void loadSpecificData(IStore store);

	public abstract void saveSpecificData(IStore store);

	@Override
	public final void load(IStore store) throws NoSuchElement {
		String string = store.getString("fieldId", null);
		this.column = this.getColumn(string);
		if (this.column == null) {
			throw new NoSuchElement();
		}
		this.loadSpecificData(store);
	}

	private IColumn getColumn(String id) {
		IColumn column = this.tableModel.getColumnById(id);
		return column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.column == null) ? 0 : this.column.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		AbstractColumnFilter other = (AbstractColumnFilter) obj;
		if (this.column == null) {
			if (other.column != null) {
				return false;
			}
		} else if (!this.column.getId().equals(other.column.getId())) {
			return false;
		}
		return true;
	}

}
