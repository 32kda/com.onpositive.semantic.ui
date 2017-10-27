package com.onpositive.businessdroids.gallery;

import com.onpositive.businessdroids.mainmenu.MenuButtonDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageThumb extends LinearLayout {

	private static final float FONT_SIZE = 8f;
	protected int thumbWidth = 92;
	protected int thumbHeight = 92;
	
	protected ImageView imageView;
	protected TextView textView;

	public ImageThumb(Context context) {
		super(context);
		createContent();
	}
	
	public ImageThumb(Context context, int thumbWidth, int thumbHeight) {
		super(context);
		this.thumbWidth = thumbWidth;
		this.thumbHeight= thumbHeight;
		createContent();
	}


	public ImageThumb(Context context, AttributeSet attrs) {
		super(context, attrs);
		createContent();
	}
	
	protected void createContent() {
		setOrientation(LinearLayout.VERTICAL);
		imageView = new ImageView(getContext().getApplicationContext());
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbWidth, thumbHeight);
		params.gravity = Gravity.CENTER;
		imageView.setLayoutParams(params);
		imageView.setPadding(2,2,2,0);
		textView = new TextView(getContext());
//		int color = Color.parseColor("#f07808");
//		Paint paint = new Paint();
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		//textView.setTextSize(TypedValue.COMPLEX_UNIT_MM,4);
		textView.setPadding(2,0,2,2);
		addView(imageView);
		addView(textView);
		//setBackgroundDrawable(new MenuButtonDrawable());
	}
	
	public void setImageBitmap(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}
	
	public void setText(String text) {
		textView.setText(text);
	}

}
