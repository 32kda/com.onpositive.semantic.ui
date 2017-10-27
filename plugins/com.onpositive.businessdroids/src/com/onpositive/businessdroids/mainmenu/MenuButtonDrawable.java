package com.onpositive.businessdroids.mainmenu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public class MenuButtonDrawable extends Drawable {
	
	protected static final int PADDING = 3;
	protected static Paint paint;
	protected static Paint selectedPaint;
	protected static Paint selectedFill;
	

	static {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		paint.setStrokeWidth(6);
	    paint.setShader(new LinearGradient(0, 0, 0, 100, Color.LTGRAY, Color.DKGRAY, TileMode.MIRROR));
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.DKGRAY);
		paint.setAlpha(100);
		
		selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		selectedPaint.setStrokeWidth(6);
		selectedPaint.setShader(new LinearGradient(0, 0, 0, 100, 0xffffe617, 0xffffee5c, TileMode.MIRROR));
		selectedPaint.setAntiAlias(true);
		selectedPaint.setStyle(Style.STROKE);
		selectedPaint.setColor(Color.DKGRAY);
		
		selectedFill = new Paint();
		selectedFill.setStyle(Style.FILL);
		selectedFill.setColor(0xfffff88e);
	}
	
	protected Path clipPath;
	protected RectF roundRect;
	protected float rx = 30;
	protected float ry = 30;
	protected boolean pressed;
	GradientDrawable gradientDrawable = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{Color.LTGRAY,Color.GRAY, Color.DKGRAY});
	@Override
	public void draw(Canvas canvas) {
		if (clipPath == null) {
			Rect bounds = copyBounds();
			gradientDrawable.setBounds(bounds);
			roundRect = new RectF(bounds);
			roundRect.left += PADDING;
			roundRect.top += PADDING;
			roundRect.right -= PADDING * 2;
			roundRect.bottom -= PADDING * 2;
			clipPath = new Path();
			clipPath.addRoundRect(roundRect, rx, ry, Direction.CW);
		}
		canvas.save();
//		canvas.clipPath(clipPath);
//		gradientDrawable.draw(canvas);
		if (pressed) {
			canvas.drawRoundRect(roundRect,rx,ry,selectedFill);
			canvas.drawRoundRect(roundRect,rx,ry,selectedPaint);
		} else {
			canvas.drawRoundRect(roundRect,rx,ry,paint);
		}
//		canvas.restore();
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
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
