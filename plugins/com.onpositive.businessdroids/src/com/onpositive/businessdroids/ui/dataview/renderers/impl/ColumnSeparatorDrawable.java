package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ColumnSeparatorDrawable extends Drawable {

	protected static final int COLUMN_DIVIDER_WIDTH = 2;
	protected final int[] columnWidths;
	protected final int columnDividerColor;
	protected Paint paint = new Paint();

	public ColumnSeparatorDrawable(int[] columnWidths, int columnDividerColor) {
		this.columnWidths = columnWidths;
		this.columnDividerColor = columnDividerColor;
		this.paint.setColor(columnDividerColor);
	}

	@Override
	public void draw(Canvas canvas) {
		int position = 0;
		if (this.columnWidths.length > 0) {
			for (int i = 0; i < this.columnWidths.length - 1; i++) {
				position += this.columnWidths[i];
				if (this.columnWidths[i + 1] == 0) {
					break;
				}
				// path.moveTo(x,0);
				int x = position;
				Rect rect = new Rect(x, 0, x
						+ ColumnSeparatorDrawable.COLUMN_DIVIDER_WIDTH,
						Integer.MAX_VALUE);
				canvas.drawRect(rect, this.paint);
			}
			position += this.columnWidths[this.columnWidths.length - 1];
		}

	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

}
