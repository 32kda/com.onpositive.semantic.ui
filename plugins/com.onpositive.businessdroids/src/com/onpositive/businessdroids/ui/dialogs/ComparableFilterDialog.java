package com.onpositive.businessdroids.ui.dialogs;

import java.util.Arrays;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.editors.IFieldEditor;
import com.onpositive.businessdroids.ui.editors.TextFieldEditor;
import com.onpositive.businessdroids.ui.themes.IApplicationMessagesProvider;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class ComparableFilterDialog extends OkCancelDialog {

	protected static final String PREV_MAX_VALUES = "prevMaxValues";
	protected static final String PREV_MIN_VALUES = "prevMinValues";

	protected final ComparableFilter filter;
	protected ImageButton okButton;
	protected ImageButton cancelButton;
	protected IFieldEditor minEdit;
	protected IFieldEditor maxEdit;

	public ComparableFilterDialog(Context context, ComparableFilter filter,
			ITheme dialogTheme) {
		this(context, 0, filter, dialogTheme);
	}

	public ComparableFilterDialog(Context context, int theme,
			ComparableFilter filter, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		this.filter = filter;
		this.setTitle(dialogTheme.getLabelProvider()
				.getComparableFilterDialogTitle());
	}

	@Override
	protected View createContents() {
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		IApplicationMessagesProvider labelProvider = this.dialogTheme
				.getLabelProvider();
		IColumn column = this.filter.getColumn();
		String prevMinKey = column.getType().getSimpleName() + column.getId()
				+ ComparableFilterDialog.PREV_MIN_VALUES;
		String prevMaxKey = column.getType().getSimpleName() + column.getId()
				+ ComparableFilterDialog.PREV_MAX_VALUES;
		this.minEdit = this.createValueChooser(layout,
				labelProvider.getMinTitle(), prevMinKey);
		this.maxEdit = this.createValueChooser(layout,
				labelProvider.getMaxTitle(), prevMaxKey);
		this.minEdit.setValue(this.filter.getMin());
		this.maxEdit.setValue(this.filter.getMax());
		return layout;
	}

	protected IFieldEditor createValueChooser(LinearLayout layout,
			String minTitle, String minId) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		//FIXME
		return new TextFieldEditor(layout, this.filter.getColumn(), minTitle,
				sharedPreferences, minId, this.dialogTheme);
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.minEdit.savePreferences();
		this.maxEdit.savePreferences();
	}

	@Override
	public Bundle onSaveInstanceState() {
		this.minEdit.savePreferences();
		this.maxEdit.savePreferences();
		Bundle bundle = super.onSaveInstanceState();
		return bundle;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void performOk() {
		Object minVal = this.minEdit.getValue();
		Object maxVal = this.maxEdit.getValue();
		if ((minVal == null) && (maxVal == null)) {
			this.filter.getTableModel().removeFilter(this.filter);
			this.dismiss();
			return;
		}
		if ((minVal != null) && (maxVal != null)
				&& (((Comparable) minVal).compareTo(maxVal) > 0)) {
			// if max > min
			maxVal = null;
		}
		boolean changed = false;
		try {
			if (((minVal == null) && (minVal != this.filter.getMin()))
					|| ((minVal != null) && ((this.filter.getMin() == null) || (((Comparable) minVal)
							.compareTo(this.filter.getMin()) != 0)))) {
				changed = true;
			}
			if (((maxVal == null) && (maxVal != this.filter.getMax()))
					|| ((maxVal != null) && ((this.filter.getMax() == null) || (((Comparable) maxVal)
							.compareTo(this.filter.getMax()) != 0)))) {
				changed = true;
			}
		} catch (NullPointerException e) {
			changed = true;
		}
		IFilter[] fieldFilters = this.filter.getTableModel()
				.getFieldFilters(this.filter.getColumn());
		if (changed || (Arrays.asList(fieldFilters).indexOf(this.filter) < 0)) {
			this.filter.setMin((Comparable) minVal);
			this.filter.setMax((Comparable) maxVal);
			this.filter.getTableModel().addFilter(this.filter);
		}
		this.dismiss();
	}
}
