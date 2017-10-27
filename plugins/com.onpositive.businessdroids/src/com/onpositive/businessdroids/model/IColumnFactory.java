package com.onpositive.businessdroids.model;

import com.onpositive.businessdroids.ui.dataview.renderers.IEditableColumn;

public interface IColumnFactory {

	IEditableColumn[] createColumns(String ...ids);
	
	IEditableColumn createColumn(String id);
}
