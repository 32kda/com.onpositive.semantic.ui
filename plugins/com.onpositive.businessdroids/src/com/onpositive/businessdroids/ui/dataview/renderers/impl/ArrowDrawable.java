package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.ui.dataview.actions.SortAction;


public class ArrowDrawable extends Drawable {

	protected final Path pathUp = new Path();
	protected final Path pathDown = new Path();
	protected Paint paint;
	protected Path currentPath = null;
	protected Point offset;
	protected final IField field;
	protected float baseSize = 10;
	protected final int sortArrowColor;
	protected final int sortArrowBorderColor;

	public ArrowDrawable(IField field, int sortArrowColor,
			int sortArrowBorderColor) {
		this.field = field;
		this.sortArrowColor = sortArrowColor;
		this.sortArrowBorderColor = sortArrowBorderColor;
		this.paint = new Paint();
		this.paint.setStyle(Paint.Style.FILL);
		this.fillPaths();
	}

	@Override
	public void draw(Canvas canvas) {
		if (this.currentPath != null) {
			this.paint.setColor(this.sortArrowColor);
			if (this.offset != null) {
				this.currentPath.offset(this.offset.x, this.offset.y);
			}
			this.paint.setStyle(Paint.Style.FILL);

			canvas.drawPath(this.currentPath, this.paint);
			this.paint.setStyle(Paint.Style.STROKE);
			this.paint.setStrokeWidth(1);
			this.paint.setAntiAlias(true);
			this.paint.setDither(true);
			this.paint.setColor(this.sortArrowBorderColor);

			canvas.drawPath(this.currentPath, this.paint);
			if (this.offset != null) {
				this.currentPath.offset(-this.offset.x, -this.offset.y);
			}
		}
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	public void setArrowState(int state) {
		switch (state) {
		case SortAction.ASCENDING:
			this.currentPath = this.pathUp;
			break;
		case SortAction.DESCENDING:
			this.currentPath = this.pathDown;
			break;
		default:
			this.currentPath = null;
			break;
		}
	}

	public void setOffset(Point offset) {
		this.offset = offset;
	}

	public void setOffset(int xOffset, int yOffset) {
		if (this.offset != null) {
			this.offset.x = xOffset;
			this.offset.y = yOffset;
		} else {
			this.offset = new Point(xOffset, yOffset);
		}
	}

	
	public void modelChanged(TableModel tableModel) {
		if (tableModel.getSortField() != this.field) {
			this.setArrowState(SortAction.UNSORTED);
		} else {
			if (tableModel.isAscendingSort()) {
				this.setArrowState(SortAction.ASCENDING);
			} else {
				this.setArrowState(SortAction.DESCENDING);
			}
		}
	}

	@Override
	public int getMinimumWidth() {
		return 30;
	}

	public void setBaseSize(float baseSize) {
		this.baseSize = baseSize;
		this.fillPaths();
	}

	protected void fillPaths() {
		this.pathUp.reset();
		this.pathDown.reset();

		float step = this.baseSize / 2;

		this.pathUp.moveTo(0, step);
		this.pathUp.lineTo(step, 0);
		this.pathUp.lineTo(step * 2, step);
		this.pathUp.lineTo(0, step);

		this.pathDown.moveTo(0, 0);
		this.pathDown.lineTo(step, step);
		this.pathDown.lineTo(step * 2, 0);
		this.pathDown.lineTo(0, 0);
	}

	public void aggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IColumn column) {
		
	}

}
