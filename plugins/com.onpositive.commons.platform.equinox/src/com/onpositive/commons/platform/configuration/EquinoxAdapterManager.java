package com.onpositive.commons.platform.configuration;

import com.onpositive.core.runtime.IAdapterManager;


public class EquinoxAdapterManager implements IAdapterManager{

	org.eclipse.core.runtime.IAdapterManager manager;
	
	public EquinoxAdapterManager(
			org.eclipse.core.runtime.IAdapterManager adapterManager) {
		this.manager=adapterManager;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object genericRegistryObject, Class adapter) {
		
		return manager.getAdapter(genericRegistryObject, adapter);
	}

}
