package com.onpositive.businessdroids.ui.editors;

import java.util.Collection;
import java.util.Collections;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class CheckBoxFieldEditor extends AbstractFieldEditor {

	public CheckBoxFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	public Object getValue() {
		boolean flag = this.check().isChecked();
		if (Number.class.isAssignableFrom(column.getType())) {
			return flag ? 1 : 0;
		}
		return flag;
	}

	@Override
	public void setValue(Object obj) {
		if (obj instanceof Boolean) {
			this.check().setChecked(((Boolean) obj).booleanValue());
		} else if (Number.class.isAssignableFrom(column.getType())
				&& obj != null) {
			Number n = (Number) obj;
			int iv = n.intValue();
			if (iv == 1) {
				this.check().setChecked(true);
			} else {
				this.check().setChecked(false);
			}
		}else if( obj == null){
			this.check().setChecked(false);
		}

	}

	protected CheckBox check() {
		return (CheckBox) control;
	}

	@Override
	public void savePreferences() {
	}

	@Override
	protected void loadPrefs(SharedPreferences prefs2, String prefId2) {
	}

	@Override
	protected View createEditComp(Class<?> type, Context context) {
		CheckBox box = new CheckBox(context);
		box.setSingleLine();
		return box;
	}

	@Override
	protected Collection<Object> getPreviousOptions() {
		return Collections.emptyList();
	}

}
