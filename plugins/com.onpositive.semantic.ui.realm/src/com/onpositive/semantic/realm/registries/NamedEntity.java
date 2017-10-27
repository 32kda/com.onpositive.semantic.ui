package com.onpositive.semantic.realm.registries;

import java.net.URL;


import org.eclipse.swt.graphics.Image;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.api.roles.ImageDescriptor;
import com.onpositive.semantic.model.api.roles.ImageManager;

public class NamedEntity extends GenericRegistryObject {

	public static final String DEFAULT_ENTITY_IMAGE = "com.onpositive.semantic.ui.realm.columnDefault";
	ImageDescriptor descriptor;
	Image image;
	
	public NamedEntity(IConfigurationElement element) {
		super(element);
	}

	public ImageDescriptor icon() {
		if (descriptor!=null){
			return descriptor;
		}
		IResourceLink resourceAttribute = getResourceAttribute("icon");
		if (resourceAttribute != null) {
			ImageDescriptor createFromURL = ImageManager.createFromLink(resourceAttribute);
			descriptor=createFromURL;
			return createFromURL;
		}
		if (descriptor == null) {
			descriptor = ImageManager.getInstance().get(
					DEFAULT_ENTITY_IMAGE)
					.getImageDescriptor();
		}
		return descriptor;
	}
	
	public Image getImage(){
		if (image!=null){
			return image;
		}
		icon();
		if (descriptor!=null){
			image=SWTImageManager.getImage(descriptor);
		}
		return image;
	}
	

	public String id() {
		return getId();
	}

	public String name() {
		return getName();
	}

}