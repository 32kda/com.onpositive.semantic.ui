package com.onpositive.semantic.realm.registries;


import com.onpositive.semantic.model.api.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.api.roles.ImageDescriptor;

public class ColumnConfigurationImageProvider implements
		IImageDescriptorProvider {

	public ColumnConfigurationImageProvider() {
	}

	
	public ImageDescriptor getImageDescriptor(Object object) {
		ColumnConfiguration cm = (ColumnConfiguration) object;
		ImageDescriptor icon = cm.getDefinition().icon();
		
		return icon;
	}
}
