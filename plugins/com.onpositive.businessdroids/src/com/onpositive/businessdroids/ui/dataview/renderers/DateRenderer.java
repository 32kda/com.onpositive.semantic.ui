package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.Date;

import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.IViewer;

import android.text.format.Time;


public class DateRenderer extends StringRenderer {

	@Override
	public CharSequence getStringFromValue(Object fieldValue,
			IViewer dataView, Object object) {
		if (fieldValue instanceof Date) {
			return PrettyFormat.format(fieldValue, true);
		}
		if (fieldValue instanceof Time) {
			Time t = (Time) fieldValue;
			long millis = t.toMillis(false);
			return PrettyFormat.format(new Date(millis), true);
		}
		return super.getStringFromValue(fieldValue, dataView, object);
	}

}
