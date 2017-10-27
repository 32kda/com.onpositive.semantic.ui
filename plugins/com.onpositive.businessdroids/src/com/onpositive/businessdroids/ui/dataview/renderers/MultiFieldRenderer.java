package com.onpositive.businessdroids.ui.dataview.renderers;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;

public class MultiFieldRenderer implements IFieldRenderer {

	protected IColumn[] columns;

	public static final int CAPTION = 0;
	public static final int DESCRIPTION = 1;
	public static final int STATUS = 2;
	public static final int ROLE = 3;

	private int orientation = LinearLayout.VERTICAL;

	public MultiFieldRenderer(IColumn[] columns) {
		super();
		this.columns = columns;
	}

	public final static int VERTICAL_MODE = 0;
	public final static int HORIZONTAL_MODE = 0;

	public MultiFieldRenderer(IColumn[] columns, int mode) {
		super();
		this.columns = columns;
		if (mode == HORIZONTAL_MODE) {
			orientation = LinearLayout.HORIZONTAL;
		}
	}

	@Override
	public View renderField(IField column, Object fieldValue, IViewer table,
			Object object) {
		LinearLayout ls = new LinearLayout(table.getContext());
		ls.setOrientation(orientation);

		for (int a = 0; a < columns.length; a++) {

			IFieldRenderer renderer = columns[a].getRenderer();
			if (renderer == null) {
				renderer = table.getViewRendererService().getRenderer(
						columns[a]);
			}
			View renderField = renderer.renderField(columns[a],
					columns[a].getPropertyValue(object), table, object);
			try {
				renderField.measure(MeasureSpec.UNSPECIFIED,
						MeasureSpec.UNSPECIFIED);
			} catch (NullPointerException e) {
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (orientation == LinearLayout.HORIZONTAL) {
				layoutParams.rightMargin = 10;			
			}
			ls.addView(renderField, layoutParams);
		}

		return ls;
	}

	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj) {
		LinearLayout ls = (LinearLayout) renderedField;
		for (int a = 0; a < columns.length; a++) {
			IFieldRenderer renderer = columns[a].getRenderer();
			if (renderer == null) {
				renderer = table.getViewRendererService().getRenderer(
						columns[a]);
			}
			View childAt = ls.getChildAt(a);
			renderer.setPropValueToView(childAt, columns[a],
					columns[a].getPropertyValue(parenObj), table, parenObj);
		}
	}

}
