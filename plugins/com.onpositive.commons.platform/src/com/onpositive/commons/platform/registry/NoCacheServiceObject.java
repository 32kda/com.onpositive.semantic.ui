package com.onpositive.commons.platform.registry;

import com.onpositive.core.runtime.IConfigurationElement;

public class NoCacheServiceObject<T> extends ServiceObject<T>{

	public NoCacheServiceObject(IConfigurationElement element) {
		super(element);
	}

	@Override
	protected boolean cachePrimary() {
		return false;
	}
}
