package com.onpositive.commons.platform.configuration;

public class DefaultConfigurationPersistenceDelegate implements
		IConfigurationPersistenceDelegate<Object> {

	public Object load(Class<Object> obj, IAbstractConfiguration config) {
		return ConfigurationPersistence.internalRestore(config, obj);
	}

	public void store(Object obj, IAbstractConfiguration config) {
		ConfigurationPersistence.internalStore(config, obj);
	}

}
