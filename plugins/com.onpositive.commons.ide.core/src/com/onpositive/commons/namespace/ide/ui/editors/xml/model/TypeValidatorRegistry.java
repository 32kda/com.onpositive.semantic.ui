package com.onpositive.commons.namespace.ide.ui.editors.xml.model;


import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.Platform;

public class TypeValidatorRegistry extends RegistryMap<GenericRegistryObject> {

	private TypeValidatorRegistry() {
		super("com.onpositive.commons.ide.core.typeValidator",
				GenericRegistryObject.class);
	}

	private static TypeValidatorRegistry registry;

	public static TypeValidatorRegistry getRegistry() {
		if (registry == null) {
			registry = new TypeValidatorRegistry();
		}
		return registry;
	}
	
	public static ITypeValidator getTypeValidator(String type) {
		getRegistry();
		final GenericRegistryObject genericRegistryObject = registry.get(type);
		if (genericRegistryObject != null) {
			try {
				return (ITypeValidator) genericRegistryObject
						.getObject();
			} catch (final CoreException e) {
				Platform.log(e);
			}
		}
		return null;
	}
}
