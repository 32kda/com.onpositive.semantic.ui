package com.onpositive.businessdroids.ui.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.Button;

public class GoodButton extends Button {

	Object object;
	String text;
	Bitmap bitmap;

	public GoodButton(Context context, Bitmap bm) {
		super(context);
		this.bitmap = bm;
	}

	@Override
	public void onDraw(Canvas canvas) {
		boolean pressed = this.isPressed();
		int pdng = (getWidth() - getHeight()) / 2;

		canvas.drawColor(Color.BLACK);
		RectF rect1 = new RectF(pdng, 0, getHeight() + pdng, getHeight() - 1);
		RectF rect2 = new RectF(pdng, 0, getHeight() + pdng,
				(getHeight() - 1) / 2.1f);

		Paint paint1 = new Paint();
		float[] pos1 = { 0, 1 };
		int[] colors1 = { Color.parseColor(!pressed ? "#dddddd" : "#bbbbbb"),
				Color.parseColor(!pressed ? "#555555" : "#444444") };
		LinearGradient shader = new LinearGradient(0f, 0f, 0f,
				getHeight() - 1f, colors1, pos1, Shader.TileMode.MIRROR);
		paint1.setShader(shader);

		Paint paint2 = new Paint();
		float[] pos = { 0, 1 };
		int[] colors = { Color.parseColor(!pressed ? "#f7f7f7" : "#dddddd"),
				Color.parseColor(!pressed ? "#c9c9c9" : "#aaaaaa") };
		shader = new LinearGradient(0f, 0f, 0f, (getHeight() - 1) / 2.1f,
				colors, pos, Shader.TileMode.MIRROR);
		paint2.setShader(shader);

		canvas.drawRoundRect(rect1, 10, 10, paint1);
		canvas.drawRoundRect(rect2, 10, 10, paint2);

		Paint paint = new Paint();
		paint.setAlpha(200);

		if (bitmap != null) {
			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth() - 1,
					bitmap.getHeight() - 1), rect1, paint);
		}
	}
}
