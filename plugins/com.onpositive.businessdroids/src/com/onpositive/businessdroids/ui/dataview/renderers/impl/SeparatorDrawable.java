package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class SeparatorDrawable extends Drawable {

	protected int lastHeight = 0;
	protected Path path = new Path();
	protected Paint paint;

	public SeparatorDrawable(int color) {

		this.paint = new Paint();
		this.paint.setColor(color);
		this.paint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void draw(Canvas canvas) {
		int height = canvas.getHeight();
		this.path = this.getPath(height);
		canvas.drawPath(this.path, this.paint);
	}

	protected Path getPath(int height) {
		if (height != this.lastHeight) {
			this.path.reset();
			this.path.addRect(0, 0, 2, height, Direction.CCW);
		}
		return this.path;
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

}
