package com.onpositive.businessdroids.ui.dataview.renderers;

import java.text.NumberFormat;

import com.onpositive.businessdroids.ui.IViewer;


public class NumberRenderer extends StringRenderer {

	@Override
	public String getStringFromValue(Object fieldValue,
			IViewer tableModel, Object object) {
		try {
			if (fieldValue == null) {
				return "";
			}
			return NumberFormat.getInstance().format(fieldValue);
		} catch (Exception e) {
			return fieldValue.toString();
		}
	}

}
