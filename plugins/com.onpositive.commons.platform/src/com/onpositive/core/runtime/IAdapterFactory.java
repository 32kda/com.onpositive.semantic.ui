package com.onpositive.core.runtime;

public interface IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType);
	
	public Class[] getAdapterList();
}
