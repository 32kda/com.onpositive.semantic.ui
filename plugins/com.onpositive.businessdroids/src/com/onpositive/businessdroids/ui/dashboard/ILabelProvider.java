package com.onpositive.businessdroids.ui.dashboard;

import android.graphics.Bitmap;

public interface ILabelProvider {

	String getText(Object element);
	
	Bitmap getBitmap(Object element);
}
