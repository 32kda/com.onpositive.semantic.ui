package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

public interface IKnowsLongestValue {

	Object longestValue(IColumn column, StructuredDataView model);
}
