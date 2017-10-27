package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

public interface IFooterRenderer extends ITablePartRenderer {

	public void setFieldFooterRenderer(IColumn column, IFieldRenderer renderer);

	public boolean needToRenderFooter(StructuredDataView dataView);

	public void updateAggregatedValues(StructuredDataView dataView);
}
