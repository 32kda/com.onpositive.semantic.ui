package com.onpositive.core.runtime;

public interface IAdapterManager {

	@SuppressWarnings("rawtypes")
	Object getAdapter(Object genericRegistryObject, Class adapter);

}
