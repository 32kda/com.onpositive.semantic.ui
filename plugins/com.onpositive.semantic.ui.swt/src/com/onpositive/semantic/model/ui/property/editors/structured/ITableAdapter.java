package com.onpositive.semantic.model.ui.property.editors.structured;

public interface ITableAdapter {

	public int[] getColumnOrder ();
	public void setColumnOrder (int[] order);
	
	public int getColumnCount ();
	public IColumnAdapter getColumn(int index);
}
