package com.onpositive.semantic.ui.labels;


import com.onpositive.semantic.model.api.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.api.roles.ImageDescriptor;
import com.onpositive.semantic.model.api.roles.ImageManager;

public class CheckboxImageDescriptorProvider implements
		IImageDescriptorProvider {

	public CheckboxImageDescriptorProvider() {
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		if (object.equals(true)) {
			return ImageManager.getImageDescriptor("true_check");
		}
		return ImageManager.getImageDescriptor("false_check");
	}

}
