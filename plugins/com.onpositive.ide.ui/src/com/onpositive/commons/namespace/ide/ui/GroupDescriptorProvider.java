package com.onpositive.commons.namespace.ide.ui;

import com.onpositive.commons.namespace.ide.ui.core.GroupRegistry;
import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class GroupDescriptorProvider implements
		IImageDescriptorProvider {

	public GroupDescriptorProvider() {
	}

	
	public ImageDescriptor getImageDescriptor(Object object) {
		String trim = object.toString().trim();
		if (trim.length()==0){
			return ImageManager.getInstance().get("com.onpositive.ide.ui.category.image").getImageDescriptor();
		}
		final GenericRegistryObject genericRegistryObject = GroupRegistry
				.getRegistry().get(trim);
		if (genericRegistryObject!=null){
		final IResourceLink resourceAttribute = genericRegistryObject
				.getResourceAttribute("icon");
		return ImageManager.createFromLink(resourceAttribute);
		}
		return ImageManager.getInstance().get("com.onpositive.semantic.ui.images.deleted").getImageDescriptor();
	}

}
