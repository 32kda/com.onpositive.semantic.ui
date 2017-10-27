package com.onpositive.businessdroids.ui.dialogs;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IStringRenderer;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;


public class TableItemSelectDialog extends ObjectSelectDialog {

	protected final IColumn column;
	protected StructuredDataView dataView;

	public TableItemSelectDialog(int theme, List<Object> items,
			StructuredDataView dataView, IColumn column,
			Collection<?> selectedValues, ITheme dialogTheme) {
		super(dataView.getContext(), theme, items, selectedValues, dialogTheme);
		this.column = column;
		this.dataView = dataView;
	}

	public TableItemSelectDialog(List<Object> items,
			StructuredDataView dataView, IColumn column,
			Collection<?> selectedValues, ITheme dialogTheme) {
		super(dataView.getContext(), items, selectedValues, dialogTheme);
		this.column = column;
		this.dataView = dataView;
	}

	@Override
	protected View createView(final Object item) {
		IFieldRenderer renderer = this.dataView.getRenderer(this.column);
		View renderedField = renderer.renderField(this.column, item,
				this.dataView, null);
		this.configureViewAppearance(renderedField);
		return renderedField;
	}

	protected void configureViewAppearance(View renderedField) {
		if (renderedField instanceof TextView) {
			((TextView) renderedField).setTextColor(Color.WHITE);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doSort(List items2) {
		if (items2.size() > 0) {
			if (items2.get(0) instanceof Comparable) {
				try{
				Collections.sort(items2);				
				return;
				}catch (ClassCastException e) {
					//ignore it here
				}
			} 
			{
				final IFieldRenderer renderer = this.dataView
						.getRenderer(this.column);
				if (renderer instanceof IStringRenderer) {
					Collections.sort(items2, new Comparator<Object>() {

						@Override
						public int compare(Object object1, Object object2) {
							CharSequence val1 = ((IStringRenderer) renderer)
									.getStringFromValue(
											object1,
											TableItemSelectDialog.this.dataView,
											null);
							CharSequence val2 = ((IStringRenderer) renderer)
									.getStringFromValue(
											object2,
											TableItemSelectDialog.this.dataView,
											null);
							return val1.toString().compareTo(val2.toString());
						}
					});
				}
			}
		}
	}

	@Override
	protected Class<?> getType() {
		return this.column.getType();
	}

}
