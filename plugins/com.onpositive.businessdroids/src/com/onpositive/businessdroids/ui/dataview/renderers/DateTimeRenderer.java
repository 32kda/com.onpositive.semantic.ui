package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.IViewer;

import android.text.format.DateUtils;
import android.text.format.Time;


public class DateTimeRenderer extends StringRenderer {
	@Override
	public CharSequence getStringFromValue(Object fieldValue,
			IViewer dataView, Object object) {
		if (fieldValue instanceof Date) {
			Formatter f = new Formatter(new StringBuilder(50), Locale.GERMAN); // TODO
																				// locale
																				// selection
			long time = ((Date) fieldValue).getTime();
			return DateUtils.formatDateRange(dataView.getContext(), f, time,
					time, DateUtils.FORMAT_UTC).toString();
		}
		if (fieldValue instanceof Time) {
			return ((Time) fieldValue).format2445();
		}
		return super.getStringFromValue(fieldValue, dataView, object);
	}
}
