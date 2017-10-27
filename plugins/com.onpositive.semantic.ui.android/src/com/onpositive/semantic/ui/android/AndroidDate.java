package com.onpositive.semantic.ui.android;

import java.util.Date;
import java.util.GregorianCalendar;

import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

@Deprecated
/**
 * <b>Deprecated</b> use AndroidDateTimeEditor instead 
 *
 */
public class AndroidDate extends SimpleAndroidEditor {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -8360007640729547486L;
	private DatePicker datePicker;
	private OnDateChangedListener onDateChangedListener = new OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			commitToBinding(getValue());
		}
	};

	@Override
	protected void resetValue() {

	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		datePicker = new DatePicker(context);
		GregorianCalendar cl = new GregorianCalendar();
		datePicker.init(cl.get(GregorianCalendar.YEAR),
				cl.get(GregorianCalendar.MONTH),
				cl.get(GregorianCalendar.DAY_OF_MONTH), onDateChangedListener);
		return datePicker;
	}

	public Date getValue() {
		GregorianCalendar cl = new GregorianCalendar();
		cl.clear();
		cl.set(GregorianCalendar.YEAR, datePicker.getYear());
		cl.set(GregorianCalendar.MONTH, datePicker.getMonth());
		cl.set(GregorianCalendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		return cl.getTime();
	}

	public void setValue(Date date) {
		GregorianCalendar cl = new GregorianCalendar();
		cl.setTime(date);
		datePicker.init(cl.get(GregorianCalendar.YEAR),
				cl.get(GregorianCalendar.MONTH),
				cl.get(GregorianCalendar.DAY_OF_MONTH), onDateChangedListener);
	}

	@Override
	public boolean needsLabel() {
		return true;
	}
}
