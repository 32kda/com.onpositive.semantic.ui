package com.onpositive.semantic.language.model;


import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.core.runtime.IConfigurationElement;


public class DocumentationContributionDescriptor extends GenericRegistryObject{

	public DocumentationContributionDescriptor(IConfigurationElement element) {
		super(element);
	}

	
	public String getId() {
		return this.getStringAttribute("url", null);
	}

}
