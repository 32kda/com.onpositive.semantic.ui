package com.onpositive.commons.platform.registry;

import java.util.IdentityHashMap;

public class ServiceMap<T extends ServiceObject<?>> extends RegistryMap<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ServiceMap(String string, Class class1) {
		super(string, class1);
		if (!ServiceObject.class.isAssignableFrom(class1)) {
			throw new IllegalArgumentException(	"class should be descendant of ServiceObject" ) ;//$NON-NLS-1$
		}
	}

	private final IdentityHashMap<Class<?>, T> values = new IdentityHashMap<Class<?>, T>();

	public T get(Class<?> class1) {
		final T internalGet = this.internalGet(class1);
		this.values.put(class1, internalGet);
		return internalGet;
	}

	private T internalGet(Class<?> class1) {
		T genericRegistryObject = this.get(class1.getName());
		if (genericRegistryObject != null) {
			return genericRegistryObject;
		} else {
			final Class<?> superclass = class1.getSuperclass();
			final Class<?>[] interfaces = class1.getInterfaces();
			
			for ( int i = 0 ; i < interfaces.length ; i++ )
			{
				genericRegistryObject = this.get(interfaces[i].getName());
				if (genericRegistryObject != null) {
					return genericRegistryObject;
				}
			}
			if (superclass != null) {
				genericRegistryObject = this.get(superclass.getName());
				if (genericRegistryObject != null) {
					return genericRegistryObject;
				}
				final T object = this.get(superclass);
				if (object != null) {
					return object;
				}
			}
			for (int i = 0; i < interfaces.length; i++) {
				final T object = this.get(interfaces[i]);
				if (object != null) {
					return object;
				}
			}
		}
		return null;
	}
}