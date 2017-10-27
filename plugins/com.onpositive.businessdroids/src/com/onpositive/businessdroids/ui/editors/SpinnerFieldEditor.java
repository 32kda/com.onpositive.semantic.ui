package com.onpositive.businessdroids.ui.editors;

import java.util.Collection;
import java.util.Collections;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SpinnerFieldEditor extends AbstractFieldEditor {

	private ArrayAdapter<Object> adapter;

	public SpinnerFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	public Object getValue() {
		return ((Spinner) this.control).getSelectedItem();
	}

	@Override
	public void setValue(Object obj) {
		int position = this.adapter.getPosition(obj);
		if (position != -1) {
			((Spinner) this.control).setSelection(position);
		}
	}

	@Override
	public void savePreferences() {
	}

	@Override
	protected void loadPrefs(SharedPreferences prefs2, String prefId2) {
	}

	@Override
	protected View createEditComp(Class<?> type, final Context context) {
		Spinner sp = new Spinner(context);
		this.adapter = new ArrayAdapter<Object>(context, 0) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Object item = this.getItem(position);
				return SpinnerFieldEditor.this.toView(item, context);
			};

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				Object item = this.getItem(position);
				View view = SpinnerFieldEditor.this.toView(item, context);
				view.setMinimumHeight(dialogTheme.getMinListItemHeight(getContext()));
				return view;
			};

		};

		sp.setAdapter(this.adapter);
		return sp;
	}

	protected View toView(Object item, Context ct) {
		EditText text = new EditText(ct);
		TextView textView = new TextView(ct);
		textView.setTextColor(text.getCurrentTextColor());
		if (item != null) {
			textView.setText(item.toString());// FIXME
		}
		return textView;
	}

	@Override
	protected Collection<Object> getPreviousOptions() {
		return Collections.emptyList();
	}

	public void setValues(Object[] strings) {
		this.adapter.setNotifyOnChange(false);
		for (Object o : strings) {
			this.adapter.add(o);
		}
		this.adapter.setNotifyOnChange(true);
		this.adapter.notifyDataSetChanged();
	}

}
