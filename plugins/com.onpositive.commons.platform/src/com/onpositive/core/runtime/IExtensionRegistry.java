package com.onpositive.core.runtime;


public interface IExtensionRegistry {

	IConfigurationElement[] getConfigurationElementsFor(String point);

}
