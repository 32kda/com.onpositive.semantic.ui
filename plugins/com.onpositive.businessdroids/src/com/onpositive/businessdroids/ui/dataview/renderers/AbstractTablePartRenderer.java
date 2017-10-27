package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;


public abstract class AbstractTablePartRenderer implements ITablePartRenderer {

	@Override
	public View render(StructuredDataView dataView, int maxWidth) {
		Context context = dataView.getContext();
		LinearLayout resultView = new LinearLayout(context);
		resultView.setOrientation(LinearLayout.HORIZONTAL);
		int sum = 0;
		IColumn[] columns = dataView.getColumns();
		int[] columnWidths = new int[columns.length];
		int realCount = 0;
		for (int i = 0; i < columns.length; i++) {
			int width = dataView.getFieldWidth(i);
			if (width == 0) {
				continue;
			}
			if (columns[i].getVisible() == IColumn.AUTOMATIC) {
				if (sum + width > maxWidth) {
					width = maxWidth - sum;
				}
				if (width <= 0) {
					continue;
				}
			}// Else column should be always visible
			if(i==columns.length-1 && sum + width < maxWidth){
				width = maxWidth-sum ;
			}
			columnWidths[i] = width;
			sum += width;
			// if (fields[i].isCaption())
			// layoutParams = new
			// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,50);
			View renderedButton = this.createView(columns[i], dataView);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					width, LayoutParams.FILL_PARENT);
			// renderedButton.setBackgroundDrawable(bgDrawables[i]);
			resultView.addView(renderedButton, layoutParams);
			realCount++;
		}
		this.setupBgDrawables(dataView, resultView, columnWidths, realCount);
		return resultView;
	}

	protected void setupBgDrawables(StructuredDataView dataView,
			LinearLayout resultView, int[] columnWidths, int realCount) {
		int[] realWidths = new int[realCount];
		int current = 0;
		for (int columnWidth : columnWidths) {
			if (columnWidth != 0) {
				realWidths[current] = columnWidth;
				current++;
			}
		}
		Drawable[] bgDrawables = this.getBackgroundDrawables(dataView,
				realWidths);
		for (int i = 0; i < bgDrawables.length; i++) {
			View child = resultView.getChildAt(i);
			child.setBackgroundDrawable(bgDrawables[i]);
		}
	}

	protected abstract Drawable[] getBackgroundDrawables(
			StructuredDataView dataView, int[] columnWidths);

	protected abstract View createView(IColumn column,
			StructuredDataView dataView);

	/**
	 * Calculates header controls pref widths and changes fieldWidths to match
	 * minimal size needed by column
	 * 
	 * @return fiieldWidths map itself
	 */
	@Override
	public int measureField(StructuredDataView dataView, IColumn column) {
		View button = this.createView(column, dataView);
		ITheme currentTheme = dataView.getCurrentTheme();
		int baseLeftPadding = currentTheme.getBaseLeftPadding();
		int baseRightPadding = currentTheme.getBaseRightPadding();
		button.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int measuredWidth = button.getMeasuredWidth() + baseLeftPadding
				+ baseRightPadding;
		return measuredWidth;
	}

}
