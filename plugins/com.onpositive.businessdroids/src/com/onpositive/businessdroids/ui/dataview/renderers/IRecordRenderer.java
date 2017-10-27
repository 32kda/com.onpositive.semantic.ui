package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.view.View;


public interface IRecordRenderer extends ITablePartRenderer {

	public View renderRecord(Object record, StructuredDataView tableModel,
			int position, int maxWidth, View convertView);

	public void setRecordToView(View convertView, Object record,
			StructuredDataView tableModel, int position);

	public boolean isReusableView(View convertView, Object item, int position,
			AbstractViewer dataView);


	void recycled(AbstractViewer dataView, View view);

}
