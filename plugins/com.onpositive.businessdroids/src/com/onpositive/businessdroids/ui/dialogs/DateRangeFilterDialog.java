package com.onpositive.businessdroids.ui.dialogs;

import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.ui.editors.IFieldEditor;
import com.onpositive.businessdroids.ui.editors.LineDateFieldEditor;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;


public class DateRangeFilterDialog extends ComparableFilterDialog {

	public DateRangeFilterDialog(Context context, ComparableFilter filter,
			ITheme dialogTheme) {
		super(context, filter, dialogTheme);
	}

	@Override
	protected IFieldEditor createValueChooser(LinearLayout layout,
			String minTitle, String minId) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		// return new DateFieldEditor(layout, filter.getField(), minTitle,
		// sharedPreferences,minId,dialogTheme);
		return new LineDateFieldEditor(layout, this.filter.getColumn(),
				minTitle, sharedPreferences, minId, this.dialogTheme);
	}

}
