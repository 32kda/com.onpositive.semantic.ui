package com.onpositive.businessdroids.ui.editors;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;


public class LineDateFieldEditor extends AbstractDateFieldEditor {

	private static final String HINT = "click...";
	java.text.DateFormat dateFormat;

	public LineDateFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	protected Date getDate() {
		return (Date) this.getValue();
	}

	@Override
	public Object getValue() {
		String text = this.editText().getText().toString();
		if (text.length() > 0) {
			try {
				return this.getDateFormat().parse(text);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	protected DateFormat getDateFormat() {
		if (this.dateFormat == null) {
			this.dateFormat = android.text.format.DateFormat.getDateFormat(this
					.editText().getContext());
		}
		return this.dateFormat;
	}

	@Override
	public void setValue(Object obj) {
		if (obj == null) {
			this.editText().setText("");
			this.editText().setHint(LineDateFieldEditor.HINT);
		} else {
			Date date = (Date) obj;
			this.editText().setText(this.getDateFormat().format(date));
		}
	}

	protected EditText editText() {
		return (EditText) this.control;
	}

	@Override
	protected View createEditComp(Class<?> type, final Context context) {
		final EditText editText = new EditText(context);
		editText.setInputType(InputType.TYPE_NULL);
		editText.setMinWidth((int) (editText.getTextSize() * 9));
		editText.setHint(LineDateFieldEditor.HINT);
		editText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return true;
				}
				LineDateFieldEditor.this.showDialog(context);
				return true;
			}
		});
		// editText.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showDialog(context);
		// }
		// });
		return editText;
	}

	protected void showDialog(final Context context) {
		OnDateSetListener callback = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				LineDateFieldEditor.this.setValue(new Date(year - 1900,
						monthOfYear, dayOfMonth));
			}
		};
		Date date = this.getDate();
		if (date == null) {
			Calendar instance = Calendar.getInstance();
			int year = instance.get(Calendar.YEAR);
			int month = instance.get(Calendar.MONTH);
			int day = instance.get(Calendar.DATE);
			date = new Date(year, month, day);
		} else {
			date.setYear(date.getYear() + 1900);
		}
		DatePickerDialog dialog = new DatePickerDialog(context, callback,
				date.getYear(), date.getMonth(), date.getDate());
		dialog.show();
	}

}
