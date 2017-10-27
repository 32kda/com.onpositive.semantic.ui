package com.onpositive.businessdroids.ui.editors;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class TextAreaFieldEditor extends TextFieldEditor {

	public TextAreaFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		super(layout, column, label, prefs, prefId, dialogTheme);
	}

	@Override
	protected View createEditComp(Class<?> type, Context context) {
		View text = super.createEditComp(type, context);
		EditText txt = (EditText) text;
		txt.setSingleLine(false);
		txt.setLines(3);
		txt.setMinLines(3);
		return text;
	}

/*	protected LinearLayout appendViewToLayout(Context context,
			View createEditComp) {
		LinearLayout ll0 = new LinearLayout(context);

		ll0.addView(createEditComp, tl);
		return ll0;
	}*/

}
