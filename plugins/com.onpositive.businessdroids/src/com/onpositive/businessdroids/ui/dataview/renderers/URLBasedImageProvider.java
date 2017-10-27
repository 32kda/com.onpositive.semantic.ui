package com.onpositive.businessdroids.ui.dataview.renderers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IAsyncFieldImageProvider;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.utils.DrawableManager;

public abstract class URLBasedImageProvider implements IAsyncFieldImageProvider {
	protected DrawableManager m=new DrawableManager();
	protected Bitmap defaultImage;
	
	@Override
	public Bitmap getImage(Context context, Object object, IField field,
			Object fvalue) {
		String url = getUrl(fvalue, object);
		if (url==null){
			return null;
		}
		if (m.containsKey(url)){
			BitmapDrawable bm= (BitmapDrawable) m.fetchDrawable(url);
			return bm.getBitmap();
		}
		return defaultImage;
	}

	@Override
	public boolean hasAllInfo(IField column, Object fieldValue, IViewer v,
			Object object) {
		return m.containsKey(getUrl(fieldValue,object));
	}

	@Override
	public Object getUpdateKey(IField column, Object fieldValue, IViewer table,
			Object parenObj) {
		return getUrl(fieldValue,parenObj);
	}

	protected abstract String getUrl(Object fieldValue, Object parenObj);

	@Override
	public void doGet(Object key, IField c, IViewer t) {
		m.fetchDrawable((String) key);
	}

}
