package com.onpositive.businessdroids.utils;

import java.util.WeakHashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageRegistry {

	protected Resources resources;
	
	public ImageRegistry(Resources resources) {
		super();
		this.resources = resources;
	}

	protected WeakHashMap<Integer, Bitmap>map=new WeakHashMap<Integer, Bitmap>();
	
	public Bitmap getBitmap(int key){
		Bitmap bitmap = map.get(key);
		if (bitmap!=null){
			return bitmap;
		}
		Bitmap decodeResource = BitmapFactory.decodeResource(resources, key);
		map.put(key, decodeResource);
		return decodeResource;		
	}
}
