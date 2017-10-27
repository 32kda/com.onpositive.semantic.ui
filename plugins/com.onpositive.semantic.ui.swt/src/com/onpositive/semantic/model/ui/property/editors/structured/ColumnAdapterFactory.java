package com.onpositive.semantic.model.ui.property.editors.structured;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

public abstract class ColumnAdapterFactory {

	public static IColumnAdapter createFrom(Item item){
		IColumnAdapter adapter = null;
		if (item instanceof TableColumn){
			TableColumn tc = (TableColumn)item;
			adapter = new TableColumnAdapter(tc);
		}
		else if (item instanceof TreeColumn){
			TreeColumn tc = (TreeColumn)item;
			adapter = new TreeColumnAdapter(tc);
		}
		
		return adapter;
	}
}
