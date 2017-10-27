package com.onpositive.businessdroids.ui;

import android.content.Context;
import android.graphics.Bitmap;

import com.onpositive.businessdroids.model.IField;

public interface IFieldImageProvider {

	/**
	 * 
	 * @param context
	 * @param object
	 * @param field - field may be null
	 * @param fvalue - field value - may be null
	 * @return
	 */
	public Bitmap getImage(Context context,Object object, IField field, Object fvalue);



}
