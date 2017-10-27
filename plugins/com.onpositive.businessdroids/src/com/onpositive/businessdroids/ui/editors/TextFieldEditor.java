package com.onpositive.businessdroids.ui.editors;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.utils.PrefUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class TextFieldEditor extends AbstractFieldEditor implements
		IFieldEditor {

	protected LinkedHashSet<String> prevValues;

	public TextFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);

	}

	@Override
	protected void loadPrefs(SharedPreferences prefs, String prefId) {
		this.prevValues = PrefUtil
				.getSetFromString(prefs.getString(prefId, ""));
	}

	@Override
	protected View createEditComp(Class<?> type, Context context) {
		EditText editText = new EditText(context);
		if (Number.class.isAssignableFrom(type)) {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		if (String.class.isAssignableFrom(type)) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		editText.setSingleLine();
		editText.setMinimumWidth(200);// TODO FIX ME
		return editText;
	}

	@Override
	public Object getValue() {
		return this.extractValue(this.text().getText().toString(), this.type);
	}

	@Override
	public void setValue(Object obj) {
		this.text().setText(this.getString(obj));
	}

	protected TextView text() {
		return (TextView) this.control;
	}

	protected CharSequence getString(Object value) {
		if (value == null) {
			return "";
		}
		if ((value instanceof Float) || (value instanceof Double)) {
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			format.setGroupingUsed(false);
			return format.format(value);
		}
		return value.toString();
	}

	protected Object extractValue(String text, Class<?> type) {
		if (text.length() == 0) {
			return null;
		}
		if (Integer.class.isAssignableFrom(type)) {
			return Integer.parseInt(text);
		}
		if (Long.class.isAssignableFrom(type)) {
			return Long.parseLong(text);
		}
		if (Short.class.isAssignableFrom(type)) {
			return Short.parseShort(text);
		}
		if (Double.class.isAssignableFrom(type)) {
			
			return Double.parseDouble(text);
			
		}
		if (Float.class.isAssignableFrom(type)) {
			return Float.parseFloat(text);
		}
		return text;
	}

	@Override
	public void savePreferences() {
		String minText = this.text().getText().toString();
		if (minText.length() > 0) {
			this.prevValues.add(minText);
			if (this.prevValues.size() > AbstractFieldEditor.MAX_LAST_VALUES) {
				this.prevValues.iterator().remove();
			}
		}
		Editor editor = this.prefs.edit();
		editor.putString(this.prefId,
				PrefUtil.getStringFromCollection(this.prevValues));
		editor.commit();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Collection<Object> getPreviousOptions() {
		return (Collection) this.prevValues;
	}

}