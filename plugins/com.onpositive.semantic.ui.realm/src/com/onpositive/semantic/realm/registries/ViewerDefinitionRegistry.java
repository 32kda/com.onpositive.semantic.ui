package com.onpositive.semantic.realm.registries;

import com.onpositive.commons.platform.registry.RegistryMap;

public final class ViewerDefinitionRegistry extends
		RegistryMap<ViewerDefinition> {

	private static ViewerDefinitionRegistry instance;

	private ViewerDefinitionRegistry() {
		super("com.onpositive.semantic.ui.realm.viewerDefinition",
				ViewerDefinition.class);
	}

	public static ViewerDefinitionRegistry getInstance() {
		if (instance == null) {
			instance = new ViewerDefinitionRegistry();
		}
		return instance;
	}

	public ViewerConfiguration getConfiguration(String id) {
		ViewerDefinition viewerDefinition = get(id);
		if (id != null) {
			return viewerDefinition.getConfiguration();
		}
		return null;
	}
}