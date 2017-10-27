package com.onpositive.semantic.model.ui.property.editors.structured.columns;
import org.eclipse.swt.widgets.Table;

import com.onpositive.semantic.model.ui.property.editors.structured.IColumnAdapter;
import com.onpositive.semantic.model.ui.property.editors.structured.ITableAdapter;
import com.onpositive.semantic.model.ui.property.editors.structured.TableColumnAdapter;


public class TableAdapter implements ITableAdapter {
	
	Table table;

	public TableAdapter(Table table) {
		super();
		this.table = table;
	}

	public IColumnAdapter getColumn(int index) {
		return new TableColumnAdapter(this.table.getColumn(index));
	}

	public int getColumnCount() {
		return table.getColumnCount();
	}

	public int[] getColumnOrder() {
		return table.getColumnOrder();
	}

	public void setColumnOrder(int[] order) {
		table.setColumnOrder(order);
	}

}
