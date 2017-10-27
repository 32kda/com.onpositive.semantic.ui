package com.onpositive.commons.platform.configuration;

public interface IConfigurationPersistenceDelegate<T> {

	public void store(T obj, IAbstractConfiguration config);

	public T load(Class<T> obj, IAbstractConfiguration config);

}
