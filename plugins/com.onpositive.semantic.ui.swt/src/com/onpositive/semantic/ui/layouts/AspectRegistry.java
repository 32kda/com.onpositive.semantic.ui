package com.onpositive.semantic.ui.layouts;

import com.onpositive.semantic.model.api.roles.GatheringRoleMap;

public class AspectRegistry extends GatheringRoleMap<VisualisationAspect> {

	public AspectRegistry() {
		super(
				"com.onpositive.semantic.ui.visualisationAspects", VisualisationAspect.class); //$NON-NLS-1$
	}

	private static AspectRegistry instance;

	public static AspectRegistry getInstance() {
		if (instance == null) {
			instance = new AspectRegistry();
		}
		return instance;
	}
}
