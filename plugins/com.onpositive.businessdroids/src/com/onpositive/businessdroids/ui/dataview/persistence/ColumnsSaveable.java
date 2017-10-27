package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;

public class ColumnsSaveable implements ISaveable {

	protected final TableModel tableModel;

	public ColumnsSaveable(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	@Override
	public void save(IStore store) {
		IColumn[] columns = this.tableModel.getColumns();
		for (IColumn column : columns) {
			store.putInt(column.getId() + "_visible", column.getVisible());
		}
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		IColumn[] columns = this.tableModel.getColumns();
		for (IColumn column : columns) {
			int visible = store.getInt(column.getId() + "_visible",
					IColumn.AUTOMATIC);
			column.setVisible(visible);
		}

	}

}
