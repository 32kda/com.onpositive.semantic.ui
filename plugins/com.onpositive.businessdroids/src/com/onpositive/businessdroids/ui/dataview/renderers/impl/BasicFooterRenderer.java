package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import java.util.HashMap;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IdentityAggregator;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.renderers.AbstractTablePartRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IFooterRenderer;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class BasicFooterRenderer extends AbstractTablePartRenderer implements
		IFooterRenderer {

	HashMap<IColumn, IFieldRenderer> customRenderers = new HashMap<IColumn, IFieldRenderer>();
	HashMap<IColumn, View> fieldViews = new HashMap<IColumn, View>();

	protected IFieldRenderer getFieldFooterRenderer(IColumn column,
			Object aggregatedValue, StructuredDataView dataView) {
		IFieldRenderer renderer = this.customRenderers.get(column);
		if (renderer == null) {
			renderer = dataView.getRenderer(column);
		}

		return renderer;
	}

	@Override
	protected View createView(IColumn column, final StructuredDataView dataView) {
		final TableModel tableModel = dataView.getTableModel();
		IAggregator aggregator = column.getAggregator();
		View resultView;
		IFieldRenderer fieldFooterRenderer = null;
		if ((aggregator != null) && (aggregator != IdentityAggregator.INSTANCE)) {
			Object aggregatedValue = tableModel.getAggregatedValue(aggregator, column);
			fieldFooterRenderer = this.getFieldFooterRenderer(column,
					aggregatedValue, dataView);
			final View renderedField = fieldFooterRenderer.renderField(column,
					aggregatedValue, dataView, null);
			resultView = renderedField;
		} else {
			TextView textView = new TextView(dataView.getContext());
			resultView = textView;
		}
		this.configureView(resultView, column, tableModel, fieldFooterRenderer,
				dataView);
		return resultView;
	}

	private void configureView(final View resultView, final IColumn column,
			final TableModel tableModel,
			final IFieldRenderer fieldFooterRenderer,
			final StructuredDataView dataView) {
		ITheme currentTheme = dataView.getCurrentTheme();
		resultView.setPadding(currentTheme.getBaseLeftPadding(),
				currentTheme.getBaseTopPadding(),
				currentTheme.getBaseRightPadding(),
				currentTheme.getBaseBottomPadding());
		resultView.setOnClickListener(this.createListener(dataView, column));
		// tableModel.addAggregatorChangeListener(new
		// IAggregatorChangeListener() {
		//
		// @Override
		// public void aggregatorChanged(IAggregator oldAggregator,
		// IAggregator newAggregator, Field field) {
		// if (field == column)
		// {
		// IFieldRenderer fieldRenderer = fieldFooterRenderer;
		// if (newAggregator != null) {
		// Object aggregatedValue =
		// newAggregator.getAggregatedValue(tableModel.getValuesForField(field));
		// if (fieldRenderer == null)
		// fieldRenderer =
		// getFieldFooterRenderer(column,aggregatedValue,dataView);
		// fieldRenderer.setPropValueToView(resultView,column,aggregatedValue,dataView);
		// } else if (resultView instanceof TextView) {
		// ((TextView) resultView).setText("");
		// }
		// }
		//
		// }
		// });
		if (resultView instanceof TextView) {
			float fontShadowRadius = currentTheme.getFooterFontShadowRadius();
			if (fontShadowRadius > 0) {
				PointF shift = currentTheme.getFooterFontShadowShift();
				((TextView) resultView).setShadowLayer(fontShadowRadius,
						shift.x, shift.y,
						currentTheme.getFooterFontShadowColor());
			}
		}
		resultView.setEnabled(true);
		this.fieldViews.put(column, resultView);
	}

	protected OnClickListener createListener(final StructuredDataView dataView,
			final IColumn thisColumn) {
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dataView.onFooterClick(thisColumn, v);
			}
		};
		return clickListener;
	}

	@Override
	public void setFieldFooterRenderer(IColumn field, IFieldRenderer renderer) {
		this.customRenderers.put(field, renderer);
	}

	@Override
	public boolean needToRenderFooter(StructuredDataView dataView) {
		return dataView.hasAggregators();
	}

	@Override
	protected Drawable[] getBackgroundDrawables(StructuredDataView dataView,
			int[] columnWidths) {
		Drawable[] bgDrawables = dataView.getCurrentTheme()
				.getFooterBackgroundDrawables(dataView, columnWidths);
		return bgDrawables;
	}

	@Override
	public void updateAggregatedValues(StructuredDataView dataView) {
		final TableModel tableModel = dataView.getTableModel();
		for (IColumn column : this.fieldViews.keySet()) {
			View fieldView = this.fieldViews.get(column);
			if (fieldView.getParent() == null) {
				continue;
			}
			IAggregator aggregator = column.getAggregator();
			if (aggregator != null
					&& !(aggregator instanceof IdentityAggregator)) {
				Object aggregatedValue = tableModel.getAggregatedValue(aggregator, column);
				final IFieldRenderer fieldFooterRenderer = this
						.getFieldFooterRenderer(column, aggregatedValue,
								dataView);
				fieldFooterRenderer.setPropValueToView(fieldView, column,
						aggregatedValue, dataView, null);
			}
		}
	}

}
