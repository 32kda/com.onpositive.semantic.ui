package com.onpositive.businessdroids.ui.editors;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.utils.PrefUtil;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.ViewGroup;


public abstract class AbstractDateFieldEditor extends AbstractFieldEditor {

	protected LinkedHashSet<Long> prevValues;

	public AbstractDateFieldEditor(ViewGroup layout, IColumn column,
			String label, SharedPreferences prefs, String prefId,
			ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	public void savePreferences() {
		Date date = this.getDate();
		if (date == null) {
			return;
		}
		this.prevValues.add(date.getTime());
		if (this.prevValues.size() > AbstractFieldEditor.MAX_LAST_VALUES) {
			this.prevValues.iterator().remove();
		}
		Editor editor = this.prefs.edit();
		editor.putString(this.prefId,
				PrefUtil.getStringFromCollection(this.prevValues));
		editor.commit();
	}

	protected abstract Date getDate();

	@Override
	protected void loadPrefs(SharedPreferences prefs2, String prefId2) {
		this.prevValues = new LinkedHashSet<Long>();
		this.prevValues.clear();
		ArrayList<String> stringList = PrefUtil.getListFromString(this.prefs
				.getString(this.prefId, ""));
		for (String string : stringList) {
			this.prevValues.add(Long.parseLong(string));
		}
	}

	@Override
	protected Collection<Object> getPreviousOptions() {
		ArrayList<Object> prevValuesStrings = new ArrayList<Object>();
		java.text.DateFormat dateInstance = java.text.DateFormat
				.getDateInstance();
		for (Long value : this.prevValues) {
			Date date2 = new Date(value);
			date2.setYear(date2.getYear());
			String date = dateInstance.format(date2);
			prevValuesStrings.add(date);
		}
		return prevValuesStrings;
	}

	@Override
	protected Object convertSelectedValue(Object value) {
		if (value instanceof String) {
			java.text.DateFormat dateInstance = java.text.DateFormat
					.getDateInstance();
			try {
				Date date = dateInstance.parse((String) value);
				date.setYear(date.getYear());
				return date;
			} catch (ParseException e) {
				return null;
			}
		}
		return super.convertSelectedValue(value);
	}

}