package com.onpositive.semantic.ui.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.widgets.DateTimeEditorStyle;
import com.onpositive.semantic.model.ui.generic.widgets.IDateTimeEditor;
import com.onpositive.semantic.model.ui.generic.widgets.impl.DateTimeDelegate;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public class AndroidDateTimeEditor extends SimpleAndroidEditor implements
		IDateTimeEditor<View> {
	

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -5048934566033474062L;

	private DateTimeEditorStyle style = DateTimeEditorStyle.DATE;
	
	private OnDateSetListener dateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			DateTimeDelegate dateTimeDelegate = (DateTimeDelegate) delegate;
			dateTimeDelegate.set(year,monthOfYear,dayOfMonth);
			delegate.handleChange(AndroidDateTimeEditor.this, dateTimeDelegate.getCalendar());
			resetValue();
		}
	};
	
	private OnTimeSetListener timeSetListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			DateTimeDelegate dateTimeDelegate = (DateTimeDelegate) delegate;
			dateTimeDelegate.setTime(hourOfDay, minute);
			delegate.handleChange(AndroidDateTimeEditor.this, dateTimeDelegate.getCalendar());
			resetValue();
		}
	};
	
	
	protected void resetValue() {
		Date date = ((DateTimeDelegate) delegate).getCalendar().getTime();
		if (isCreated()) {
			String text;
			if (DateTimeEditorStyle.CALENDAR == style || DateTimeEditorStyle.DATE == style) {
				text = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(date);
			} else if (DateTimeEditorStyle.TIME == style) {
				text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date);
			} else {
				text = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.SHORT).format(date);
			}
			((TextView)widget).setText(text);;
		}
	};
	

	@Override
	protected View internalCreate(AndroidComposite cm, final Context context) {
		Button button = new Button(context);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (DateTimeEditorStyle.DATE == style || DateTimeEditorStyle.CALENDAR == style) {
					DatePickerDialog dialog = new DatePickerDialog(context, dateSetListener, ((DateTimeDelegate) delegate).getYear(), ((DateTimeDelegate) delegate).getMonth(), ((DateTimeDelegate) delegate).getDayOfMonth());
					dialog.show();
				}
				if (DateTimeEditorStyle.TIME == style) {
					TimePickerDialog dialog = new TimePickerDialog(context, timeSetListener, ((DateTimeDelegate) delegate).getHourOfDay(), ((DateTimeDelegate) delegate).getMinute(), true);
					dialog.show();
				}
			}
		});
		return button;
	}

	@Override
	public void setCalendar(GregorianCalendar calendar) {
		((DateTimeDelegate) delegate).setCalendar(calendar);
	}

	@Override
	public GregorianCalendar getCalendar() {
		return ((DateTimeDelegate) delegate).getCalendar();
	}

	@Override
	@HandlesAttributeDirectly("type")
	public void setStyle(DateTimeEditorStyle style) {
		this.style = style;
	}

	@Override
	public DateTimeEditorStyle getStyle() {
		return style;
	}
	
	@Override
	public boolean needsLabel() {
		return true;
	}

}
