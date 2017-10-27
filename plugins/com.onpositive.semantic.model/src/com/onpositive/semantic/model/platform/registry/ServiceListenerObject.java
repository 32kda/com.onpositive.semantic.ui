package com.onpositive.semantic.model.platform.registry;

import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.api.changes.IObjectListener;

public class ServiceListenerObject extends LabelObject {

	public ServiceListenerObject(IConfigurationElement element) {
		super(element);
	}

	@Override
	public Object getProvider() {
		if (provider != null) {
			return provider;
		}

		try {
			provider = getObjectAttribute("listenerClass",
					IObjectListener.class);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return provider;
	}

}
