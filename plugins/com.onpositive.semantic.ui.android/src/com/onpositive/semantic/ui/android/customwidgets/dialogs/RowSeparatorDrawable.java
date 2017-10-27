package com.onpositive.semantic.ui.android.customwidgets.dialogs;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class RowSeparatorDrawable extends Drawable {

	protected Path path = new Path();
	protected Paint paint;

	public RowSeparatorDrawable(int color) {
		this.paint = new Paint();
		this.paint.setColor(color);
		this.paint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void draw(Canvas canvas) {
		Path path = this.createPath(canvas);
		canvas.drawPath(path, this.paint);
	}

	protected Path createPath(Canvas canvas) {
		int width = Integer.MAX_VALUE;
		// int height = canvas.getHeight();
		Path resultPath = new Path();
		resultPath.addRect(0, 0, width - 1, 2, Direction.CCW);
		return resultPath;
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
