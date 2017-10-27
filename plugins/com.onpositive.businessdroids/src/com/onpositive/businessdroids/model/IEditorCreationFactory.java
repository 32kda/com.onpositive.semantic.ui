package com.onpositive.businessdroids.model;

import android.content.SharedPreferences;
import android.view.ViewGroup;

import com.onpositive.businessdroids.ui.editors.AbstractFieldEditor;
import com.onpositive.businessdroids.ui.themes.ITheme;

public interface IEditorCreationFactory {
	AbstractFieldEditor createEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme);
}
