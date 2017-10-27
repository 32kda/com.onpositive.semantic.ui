package com.onpositive.businessdroids.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface IFormSection {

	String getTitle();
	
	View createView(Context ctx, int width, int height);
	
	Drawable getIcon();
	
	boolean isEnabled();
}
