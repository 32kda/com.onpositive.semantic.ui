package com.onpositive.semantic.model.ui.roles;

import com.onpositive.semantic.model.api.labels.ILabelProvider;
import com.onpositive.semantic.model.api.meta.IService;


public interface IImageDescriptorProvider extends ILabelProvider ,IService{

	public ImageDescriptor getImageDescriptor(Object object);
}
