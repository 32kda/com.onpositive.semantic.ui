package com.onpositive.commons.namespace.ide.ui.editors.xml.model;


import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.Platform;

public class HyperlinkProviderRegistry extends
		RegistryMap<GenericRegistryObject> {

	private HyperlinkProviderRegistry() {
		super("com.onpositive.commons.ide.core.typeHyperlinkProvider",
				GenericRegistryObject.class);
	}

	private static HyperlinkProviderRegistry registry;

	public static HyperlinkProviderRegistry getRegistry() {
		if (registry == null) {
			registry = new HyperlinkProviderRegistry();
		}
		return registry;
	}
		
	public static ITypeHyperlinkProvider getCompletionProvider(String type) {
		getRegistry();
		final GenericRegistryObject genericRegistryObject = registry.get(type);
		if (genericRegistryObject != null) {
			try {
				return (ITypeHyperlinkProvider) genericRegistryObject
						.getObject();
			} catch (final CoreException e) {
				Platform.log(e);
			}
		}
		return null;
	}
}
