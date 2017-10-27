package com.onpositive.businessdroids.ui.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Icon extends ViewGroup {

	String text;
	Bitmap bitmap;
	Object element;
	private Button button;
	private TextView label;
	

	public Icon(Context context, String text, Bitmap bitmap, Object element) {
		super(context, null);
		this.text = text;
		this.bitmap = bitmap;
		this.element = element;
		
		refresh();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int width = r - l;
		int height = b - t;
		button.layout(0, 0, width,(int) (height - height / 5.0));
		label.layout(0, (int) (height - height / 5.0) + 1, width, height);
		label.setTextSize(height/11.0f);
	}

	public void refresh() {
		removeAllViews();
		button = new GoodButton(this.getContext(), bitmap);
		button.setClickable(true);
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.out.println("clicked");
            }
        });
		
		button.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				System.out.println("touched");
				
				return false;
			}
		});
				
		label = new TextView(this.getContext());
		label.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		label.setText(text);
		
		addView(button);
		addView(label);
	};
}
