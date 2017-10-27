package com.onpositive.businessdroids.ui.editors;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


public class DateFieldEditor extends AbstractDateFieldEditor {

	protected CheckBox bx;

	public DateFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	public Object getValue() {
		if (this.bx.isChecked()) {
			return null;
		}
		GregorianCalendar cl = new GregorianCalendar();
		DatePicker datePicker = this.datePicker();
		cl.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), 0, 0);
		return cl.getTime();
	}

	@Override
	protected LayoutParams getLayoutParams() {
		return AbstractFieldEditor.wrapParams;
	}

	@Override
	protected void createButtons(IColumn column, LinearLayout minLayout) {
		LinearLayout l = new LinearLayout(this.getContext());
		l.setOrientation(LinearLayout.HORIZONTAL);
		super.createButtons(column, l);
		minLayout.addView(l);
	}

	@Override
	protected int getOrientation() {
		return LinearLayout.VERTICAL;
	}

	@Override
	public void setValue(Object obj) {
		GregorianCalendar cm = new GregorianCalendar();
		Date dt = (Date) obj;
		if (dt != null) {
			cm.setTime(dt);
			DatePicker datePicker = this.datePicker();
			datePicker.updateDate(cm.get(Calendar.YEAR),
					cm.get(Calendar.MONTH), cm.get(Calendar.DAY_OF_MONTH));
			datePicker.setEnabled(true);
			this.bx.setChecked(false);
		} else {
			DatePicker datePicker = this.datePicker();
			datePicker.setEnabled(false);
			this.bx.setChecked(true);
		}
		// datePicker.set
	}

	@Override
	protected LayoutParams createContentLayout() {
		return AbstractFieldEditor.wrapParams;
	}

	DatePicker datePicker() {
		return (DatePicker) this.control;
	}

	@Override
	protected View createEditComp(Class<?> type, Context context) {
		return new DatePicker(context);
	}

	@Override
	protected Date getDate() {
		DatePicker datePicker = this.datePicker();
		Date date = new Date(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth());
		return date;
	}

	@Override
	protected void addClearButton(LinearLayout minLayout) {
		this.bx = new CheckBox(this.getContext());
		this.bx.setText("Unbound");
		this.bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				DateFieldEditor.this.control.setEnabled(!isChecked);
			}
		});
		minLayout.addView(this.bx);
	}

	protected boolean isUnbound() {
		return this.bx.isChecked();
	}

}
