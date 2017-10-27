package com.onpositive.semantic.ui.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.onpositive.commons.platform.configuration.AndroidDrawableLink;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.model.ui.roles.ResourceLinkDescriptor;
import com.onpositive.semantic.model.ui.roles.URLImageDescriptor;

public class AndroidImageManager{
	
	static WeakHashMap<ImageDescriptor, Drawable>map=new WeakHashMap<ImageDescriptor, Drawable>();

	public static Drawable getImage(Object object,String role,String theme){
		ImageDescriptor ds= ImageManager.getInstance().getImageDescriptor(object, role, theme);
		return decriptorToDrawable(ds);		
	}
	
	public static Drawable getImageDrawable(ImageDescriptor imageDescriptor) {
		return decriptorToDrawable(imageDescriptor);
	}

	protected static Drawable decriptorToDrawable(ImageDescriptor ds) {
		Drawable drawable = map.get(ds);
		if (drawable!=null){
			return drawable;
		}
		if (ds instanceof ResourceLinkDescriptor){
			IResourceLink link = ((ResourceLinkDescriptor) ds).getLink();
			if (link instanceof AndroidDrawableLink) {
				return ((AndroidDrawableLink) link).getDrawable();
			}
			ResourceLinkDescriptor m=(ResourceLinkDescriptor) ds;
			InputStream openStream = m.openStream();
			try{
			BitmapDrawable bitmapDrawable = new BitmapDrawable(openStream);
			map.put(ds, bitmapDrawable);
			return bitmapDrawable;
			}finally{
				try {
					openStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}				
			}
		}
		if (ds instanceof URLImageDescriptor){
			URLImageDescriptor m=(URLImageDescriptor) ds;
			String url=m.getUrl();
			url=url.substring(url.indexOf("://")+3);			
			BitmapDrawable bitmapDrawable = new BitmapDrawable(Platform.getBundle("android").getResourceAsStream(url));
			map.put(ds, drawable);
			return bitmapDrawable;			
		}
		return null;
	}

	public static Drawable getImage(String string) {
		return decriptorToDrawable(ImageManager.getImageDescriptor(string));
	}
}
