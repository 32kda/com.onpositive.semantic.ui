package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.text.format.DateUtils;
import android.text.format.Time;

import com.onpositive.businessdroids.ui.IViewer;


public class TimeRenderer extends StringRenderer {
	@Override
	public CharSequence getStringFromValue(Object fieldValue,
			IViewer dataView, Object object) {
		if (fieldValue instanceof Date) {
			Formatter f = new Formatter(new StringBuilder(50),
					Locale.getDefault());
			long time = ((Date) fieldValue).getTime();
			return DateUtils.formatDateRange(dataView.getContext(), f, time,
					time, DateUtils.FORMAT_SHOW_TIME).toString();
		}
		if (fieldValue instanceof Time) {
			return ((Time) fieldValue).format("%H:%M");
		}
		return super.getStringFromValue(fieldValue, dataView, object);
	}
}
