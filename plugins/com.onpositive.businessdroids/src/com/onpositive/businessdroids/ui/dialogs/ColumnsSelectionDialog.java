package com.onpositive.businessdroids.ui.dialogs;

import java.util.Arrays;
import java.util.Iterator;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


/**
 * Dialog for selecting visible columns
 * 
 * @author 32kda
 */
public class ColumnsSelectionDialog extends ObjectSelectDialog {

	public ColumnsSelectionDialog(Context context, int theme,
			StructuredDataView dataView, ITheme dialogTheme) {
		super(context, theme, Arrays.asList(dataView
				.getPresentationSortedColumns()), dataView.getVisibleColumns(),
				dialogTheme);
	}

	@Override
	protected String getLabel(Object item) {
		return ((IColumn) item).getId();
	}

	@Override
	public String getInitialTitle() {
		return this.dialogTheme.getLabelProvider().getColumnsDialogTitle();
	}

	public ColumnsSelectionDialog(Context context, StructuredDataView dataView,
			ITheme dialogTheme) {
		super(context, Arrays.asList(dataView.getColumns()), dataView
				.getVisibleColumns(), dialogTheme);
	}

	@Override
	protected boolean isEnabled(Object item) {
		return !((IColumn) item).isAlwaysVisible();
	}

	@Override
	protected View createView(Object item) {
		if (item instanceof IColumn) {
			TextView textView = new TextView(this.getContext());
			textView.setText(this.getLabel(item));
			textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			return textView;
		}
		return null;
	}

	@Override
	protected void deselectAll() {
		for (Iterator<Object> iterator = this.result.iterator(); iterator
				.hasNext();) {
			IColumn column = (IColumn) iterator.next();
			if (!(column).isAlwaysVisible()) {
				iterator.remove();
			}
		}
		;
		this.adapter.notifyDataSetChanged();
	}

}
