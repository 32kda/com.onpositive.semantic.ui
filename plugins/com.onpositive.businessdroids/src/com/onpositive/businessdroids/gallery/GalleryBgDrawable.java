package com.onpositive.businessdroids.gallery;

import java.util.Arrays;

import android.content.res.Resources.Theme;
import android.graphics.drawable.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Path.Direction;

import com.onpositive.businessdroids.ui.dataview.renderers.impl.HatchDrawable;

public class GalleryBgDrawable extends HatchDrawable {
	
	protected static Paint paint;
	protected static Paint selectedPaint;
	
	static {
		paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.GRAY);
		
		selectedPaint = new Paint();
		selectedPaint.setStrokeWidth(10);
		selectedPaint.setAntiAlias(true);
		selectedPaint.setStyle(Style.STROKE);
		selectedPaint.setColor(Color.parseColor("#f07808"));
	}
	
	protected Path clipPath;
	protected RectF roundRect;
	protected float rx = 10;
	protected float ry = 10;
	protected boolean pressed;
	
	public GalleryBgDrawable() {
		setOrientation(HatchDrawable.ORIENTION_LT_RB);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (clipPath == null) {
			roundRect = new RectF(copyBounds());
			clipPath = new Path();
			clipPath.addRoundRect(roundRect, rx, ry, Direction.CW);
		}
		canvas.save();
		canvas.clipPath(clipPath);
		super.draw(canvas);
		if (pressed)
			canvas.drawRoundRect(roundRect,rx,ry,selectedPaint);
		else
			canvas.drawRoundRect(roundRect,rx,ry,paint);
		canvas.restore();
	}
	
	public void setRadius(int rx, int ry) {
		this.rx = rx;
		this.ry = ry;
	}
	
	@Override
	public boolean isStateful() {
		return true;
	}

	@Override
	protected boolean onStateChange(int[] state) {
		boolean pressed = false;
		for (int i = 0; i < state.length; i++) {
			if (state[i] == android.R.attr.state_pressed || state[i] == android.R.attr.state_selected) {
				pressed = true;
				break;
			}
		}
		this.pressed = pressed;
		invalidateSelf();
		return super.onStateChange(state);
	}
}
