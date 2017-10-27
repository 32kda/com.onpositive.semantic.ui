package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.view.View;


public interface ITablePartRenderer {

	public int measureField(StructuredDataView tableModel, IColumn column);

	public View render(StructuredDataView dataView, int maxWidth);
}
