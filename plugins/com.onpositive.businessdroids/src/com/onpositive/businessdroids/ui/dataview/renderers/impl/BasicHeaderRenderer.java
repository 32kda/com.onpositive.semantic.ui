package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.renderers.AbstractTablePartRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IHeaderRenderer;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class BasicHeaderRenderer extends AbstractTablePartRenderer implements
		IHeaderRenderer {

	private static final class ColumnButton extends Button {
		private final StructuredDataView dataView;
		private final IColumn column;
		private final ArrowDrawable drawable0;
		private final ITheme currentTheme;

		private ColumnButton(Context context, StructuredDataView dataView,
				IColumn column, ArrowDrawable drawable0, ITheme currentTheme) {
			super(context);
			this.dataView = dataView;
			this.column = column;
			this.drawable0 = drawable0;
			this.currentTheme = currentTheme;
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);

			int yOffset = (this.getBaseline()) / 2;
			this.drawable0.setOffset(this.currentTheme.getSortArrowOffset(),
					yOffset);
			this.drawable0.draw(canvas);
		}

		@Override
		public int getCompoundPaddingLeft() {
			if (this.column.isCaption()) {
				if (this.dataView.getSortField() == this.column) {
					return this.currentTheme.getSortArrowPadding();
				}
				return super.getCompoundPaddingLeft();
			}
			return this.currentTheme.getSortArrowPadding();
		}

		@Override
		protected void onAttachedToWindow() {
			this.drawable0.modelChanged(this.dataView.getTableModel());
		}

		@Override
		public int getCompoundPaddingRight() {
			if (this.column.isCaption()) {
				if (this.dataView.getSortField() == this.column) {
					return super.getCompoundPaddingLeft();
				}
				return this.currentTheme.getSortArrowPadding();
			}
			return super.getCompoundPaddingRight();
		}
	}

	protected final StructuredDataView dataView;

	public BasicHeaderRenderer(StructuredDataView dataView) {
		super();
		this.dataView = dataView;

	}

	HashMap<IColumn, View> fieldViews = new HashMap<IColumn, View>();

	@Override
	public View createView(final IColumn column,
			final StructuredDataView dataView) {
		if (dataView == null) {
			throw new AssertionError("dataView shouldn't be null");
		}
		final ITheme currentTheme = dataView.getCurrentTheme();

		final ArrowDrawable drawable0 = currentTheme.getArrowDrawable(column);
		drawable0.setBounds(0, 0, 0, 0);
		final Button renderedButton = new ColumnButton(dataView.getContext(),
				dataView, column, drawable0, currentTheme);
		drawable0.setBaseSize(renderedButton.getTextSize());
		if (column.isCaption()) {
			renderedButton.setPadding(Math.max(2, drawable0.getMinimumWidth()),
					currentTheme.getHeaderTopPadding(), 5,
					currentTheme.getHeaderBottomPadding());
		} else {
			renderedButton.setPadding(2, currentTheme.getHeaderTopPadding(), 5,
					currentTheme.getHeaderBottomPadding());
		}
		// if (dataView != null) {
		renderedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataView.onHeaderClick(column, v);
				// drawable0.setArrowState(state);
			}
		});
		// }
		String id = column.getTitle();
		if (dataView.getCurrentTheme().showFilterIndicators()) {

			IFilter[] fieldFilters = dataView.getTableModel().getFieldFilters(
					column);
			if ((fieldFilters != null) && (fieldFilters.length > 0)) {
				id = id + ("*");
			}
		}
		if (dataView.renderHeadersClicable()) {
			SpannableString s = new SpannableString(id);
			s.setSpan(new UnderlineSpan(), 0, id.length(), 0);
			renderedButton.setText(s);
		}
		else{
			renderedButton.setText(id);
		}
		// renderedButton.setPadding(dataView.getCurrentTheme().getBaseLeftPadding(),2,0,2);
		if (column.isCaption()) {
			renderedButton.setGravity(Gravity.LEFT);
		}
		renderedButton.setTextColor(currentTheme.getHeaderFontColor());
		float fontShadowRadius = currentTheme.getHeaderFontShadowRadius();
		
		if (fontShadowRadius > 0) {
			PointF shift = currentTheme.getHeaderFontShadowShift();
			renderedButton.setShadowLayer(fontShadowRadius, shift.x, shift.y,
					currentTheme.getHeaderFontShadowColor());
		}
		this.fieldViews.put(column, renderedButton);
		return renderedButton;
	}

	@Override
	protected Drawable[] getBackgroundDrawables(StructuredDataView dataView,
			int[] columnWidths) {
		return dataView.getCurrentTheme().getHeaderBackgroundDrawables(
				dataView, columnWidths);
	}

	@Override
	public boolean updateHeaders(AbstractViewer structuredDataView) {

		for (IColumn column : this.fieldViews.keySet()) {
			ColumnButton view = (ColumnButton) this.fieldViews.get(column);
			String id = column.getTitle();
			if (this.dataView.getCurrentTheme().showFilterIndicators()) {
				IFilter[] fieldFilters = this.dataView.getTableModel()
						.getFieldFilters(column);
				if ((fieldFilters != null) && (fieldFilters.length > 0)) {
					id = id + ("*");
				}
				if ((view.getText() != null) && !view.getText().equals(id)) {
					return true;
				}
			}
			view.drawable0
					.modelChanged(((StructuredDataView) structuredDataView)
							.getTableModel());
		}
		return false;
	}

}
