package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;

public class BgTextDrawable extends ColorDrawable {

	protected String text;
	protected int fontColor;
	private ViewGroup parentGroup;

	public BgTextDrawable(int color, String text, ViewGroup viewGroup) {
		super(color);
		this.text = text;
		this.parentGroup = viewGroup;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if ((this.text != null) && (this.text.length() > 0)) {
			Rect bounds = this.getBounds();
			int width = this.parentGroup.getWidth();
			int height = bounds.height();
			Paint paint = new Paint();
			paint.setColor(this.fontColor);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(20);
			paint.setAntiAlias(true);
			paint.setDither(true);
			canvas.drawText(this.text, width / 2, height / 2 - 5, paint);
		}
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

}
