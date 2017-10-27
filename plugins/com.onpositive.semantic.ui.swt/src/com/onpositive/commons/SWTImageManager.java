package com.onpositive.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.roles.ResourceLinkDescriptor;
import com.onpositive.semantic.model.api.roles.URLImageDescriptor;


public class SWTImageManager {

	private static final class ResourceLinkSWTDesriptor extends ImageDescriptor {
		private final ResourceLinkDescriptor ma;
		private ImageData imageData;

		private ResourceLinkSWTDesriptor(ResourceLinkDescriptor ma) {
			this.ma = ma;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ma == null) ? 0 : ma.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ResourceLinkSWTDesriptor other = (ResourceLinkSWTDesriptor) obj;
			if (ma == null) {
				if (other.ma != null)
					return false;
			} else if (!ma.equals(other.ma))
				return false;
			return true;
		}

		@Override
		public ImageData getImageData() {
			if (imageData!=null){
				return imageData;
			}
			InputStream openStream = ma.openStream();
			try{
			imageData = new ImageData(openStream);
			return imageData;
			}finally{
				try {
					openStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static ImageDescriptor getDescriptor(String string) {
		return getImageDescriptor(ImageManager.getImageDescriptor(string));
	}

	public static Image getImage(String image) {
		return getImage(ImageManager.getImageDescriptor(image));
	}

	static HashMap<ImageDescriptor, Image>images=new HashMap<ImageDescriptor, Image>();

	public static Image getImage(
			com.onpositive.semantic.model.api.roles.ImageDescriptor imageDescriptor) {
		ImageDescriptor imageDescriptor2 = getImageDescriptor(imageDescriptor);
		if (images.containsKey(imageDescriptor2)){
			return images.get(imageDescriptor2);
		}
		Image me=imageDescriptor2.createImage();
		images.put(imageDescriptor2, me);
		return me;
	}

	public static ImageDescriptor getImageDescriptor(
			com.onpositive.semantic.model.api.roles.ImageDescriptor imageDescriptor) {
		if (imageDescriptor==null){
			return ImageDescriptor.getMissingImageDescriptor();
		}
		if (imageDescriptor instanceof URLImageDescriptor){
			URLImageDescriptor m=(URLImageDescriptor) imageDescriptor;
			return ImageDescriptor.createFromURL(m.getActualUrl());
		}
		if (imageDescriptor instanceof ResourceLinkDescriptor){
			final ResourceLinkDescriptor ma=(ResourceLinkDescriptor) imageDescriptor;
			return new ResourceLinkSWTDesriptor(ma);
		}
		if (imageDescriptor instanceof ISWTDescriptor){
			ISWTDescriptor m=(ISWTDescriptor) imageDescriptor;
			return m.getDescripror();
		}
		throw new IllegalArgumentException();
	}	

	public static Image getImage(Object m, String role, String theme) {
		return getImage(ImageManager.getInstance().getImageDescriptor(m, role, theme));
	}

	public static com.onpositive.semantic.model.api.roles.ImageDescriptor newSWTDescriptor(
			ImageDescriptor imageDescriptorFromPlugin) {	
		return new SWTImageDescriptor(imageDescriptorFromPlugin);
	}

}
