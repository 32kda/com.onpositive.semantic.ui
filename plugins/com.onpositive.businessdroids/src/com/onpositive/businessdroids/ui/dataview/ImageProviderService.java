package com.onpositive.businessdroids.ui.dataview;

import java.util.HashMap;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IFieldImageProvider;

public class ImageProviderService {

	private HashMap<IColumn, IFieldImageProvider> imageProviders = new HashMap<IColumn, IFieldImageProvider>();
	// private BasicImageProvider defaultImageProvider = new
	// BasicImageProvider();
	private IFieldImageProvider defaultImageProvider = null;

	public IFieldImageProvider getDefaultImageProvider() {
		return this.defaultImageProvider;
	}

	public void setDefaultImageProvider(IFieldImageProvider defaultImageProvider) {
		this.defaultImageProvider = defaultImageProvider;
	}

	public IFieldImageProvider getImageProvider(IField field) {
		if (field instanceof IColumn) {
			IFieldImageProvider imageProvider = ((IColumn) field).getImageProvider();
			if (imageProvider != null) {
				return imageProvider;
			}
		}
		IFieldImageProvider imageProvider = this.imageProviders.get(field);
		if (imageProvider != null) {
			return imageProvider;
		} else if (isCaption(field)) {
			return this.defaultImageProvider;
		} else {
			return null;
		}
	}

	//FIXME 
	public static boolean isCaption(IField field) {
		if (field instanceof IColumn){
			return ((IColumn)field).isCaption();
		}
		return false;
	}

	public void setImageProvider(IColumn field,
			IFieldImageProvider imageProvider) {
		this.imageProviders.put(field, imageProvider);
	}

}
