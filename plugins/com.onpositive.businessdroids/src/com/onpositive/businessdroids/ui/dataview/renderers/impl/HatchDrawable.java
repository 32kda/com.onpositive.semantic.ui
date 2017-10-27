package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Drawable for filling with a hatched fill
 * 
 * @author 32kda
 */
public class HatchDrawable extends Drawable {

	/**
	 * Left top-right bottom orientation
	 */
	public static final int ORIENTION_LT_RB = 0;
	/**
	 * Left bottom-right top orientation
	 */
	public static final int ORIENTION_LB_RT = 1;

	protected int space = 5;
	protected int color = Color.DKGRAY;
	protected int bgColor = Color.BLACK;
	protected int orientation = HatchDrawable.ORIENTION_LB_RT;
	private Paint stroke;
	
	public HatchDrawable() {
		stroke = new Paint();
		stroke.setStyle(Paint.Style.STROKE);
		stroke.setColor(this.color);
		stroke.setStrokeWidth(1);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(this.bgColor);
		int width = getBounds().width();
		int height = getBounds().height();
		int size = width + height;
		for (int i = this.space; i < size; i += this.space) {
			if (this.orientation == HatchDrawable.ORIENTION_LB_RT) {
				canvas.drawLine(0, i, i, 0, stroke);
			} else if (this.orientation == HatchDrawable.ORIENTION_LT_RB) {
				canvas.drawLine(i - height, 0, i, height, stroke);
			}
		}
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	public int getSpace() {
		return this.space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public int getColor() {
		return this.color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getBgColor() {
		return this.bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

}
