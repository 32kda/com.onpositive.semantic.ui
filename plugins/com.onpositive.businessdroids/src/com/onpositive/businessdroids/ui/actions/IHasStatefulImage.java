package com.onpositive.businessdroids.ui.actions;

import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.ui.IViewer;


public interface IHasStatefulImage extends IHasImage {

	public Drawable getStateIcon(IViewer dataView);

}
