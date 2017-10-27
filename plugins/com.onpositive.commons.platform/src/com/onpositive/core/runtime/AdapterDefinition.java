package com.onpositive.core.runtime;

import com.onpositive.commons.platform.registry.ServiceObject;

public class AdapterDefinition extends ServiceObject<IAdapterFactory>{

	public AdapterDefinition(IConfigurationElement element) {
		super(element);
	}

	@Override
	public String getId() {
		return this.getStringAttribute(OBJECT_CLASS, "")+"."+getStringAttribute("adapter", "");
	}
}
