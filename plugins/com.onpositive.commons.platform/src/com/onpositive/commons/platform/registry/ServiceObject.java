package com.onpositive.commons.platform.registry;

import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;

public class ServiceObject<T> extends GenericRegistryObject {

	protected static final String OBJECT_CLASS = "objectClass"; //$NON-NLS-1$

	public ServiceObject( IConfigurationElement element ) {
		super(element);
	}

	public String getId() {
		return this.getStringAttribute( OBJECT_CLASS, null );
	}

	@SuppressWarnings( "unchecked" )
	public T getService() {
		try {
			return (T) this.getObject();
		} catch (final CoreException e) {
			throw new IllegalStateException(e);
		}
	}
}
