package com.onpositive.commons.namespace.ide.ui.editors.xml.model;


import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.Platform;

public class CompletionProviderRegistry extends
		RegistryMap<GenericRegistryObject> {

	private CompletionProviderRegistry() {
		super("com.onpositive.commons.ide.core.typeCompletionProvider",
				GenericRegistryObject.class);
	}

	private static CompletionProviderRegistry registry;

	public static CompletionProviderRegistry getRegistry() {
		if (registry == null) {
			registry = new CompletionProviderRegistry();
		}
		return registry;
	}
		
	public static ITypeCompletionProvider getCompletionProvider(String type) {
		getRegistry();
		final GenericRegistryObject genericRegistryObject = registry.get(type);
		if (genericRegistryObject != null) {
			try {
				return (ITypeCompletionProvider) genericRegistryObject
						.getObject();
			} catch (final CoreException e) {
				Platform.log(e);
			}
		}
		return null;
	}
}
