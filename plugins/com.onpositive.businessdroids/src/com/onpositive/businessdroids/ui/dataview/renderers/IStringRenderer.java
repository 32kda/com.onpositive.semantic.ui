package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.ui.IViewer;

public interface IStringRenderer {

	public abstract CharSequence getStringFromValue(Object fieldValue,
			IViewer tableModel, Object object);

}