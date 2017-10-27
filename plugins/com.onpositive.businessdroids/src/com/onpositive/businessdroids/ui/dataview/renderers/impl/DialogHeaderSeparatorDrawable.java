package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;

public class DialogHeaderSeparatorDrawable extends RowSeparatorDrawable {

	public DialogHeaderSeparatorDrawable(int color) {
		super(color);
	}

	@Override
	protected Path createPath(Canvas canvas) {
		// int width = Integer.MAX_VALUE;
		Path resultPath = new Path();
		int bottom = this.getBounds().bottom;
		resultPath.addRect(5, bottom - 5, this.getBounds().right - 5,
				bottom - 3, Direction.CCW);
		return resultPath;
	}
}
