package com.onpositive.semantic.realm.registries;

import com.onpositive.core.runtime.IConfigurationElement;

public class FilterDefinitionObject extends NamedEntity {

	public FilterDefinitionObject(IConfigurationElement element) {
		super(element);
	}

	public String viewerDefinition() {
		return "targetViewer";
	}

	public FilterConfiguration createFilterConfiguration(
			FilterDefinitionObject columnDefinitionObject) {
		return null;
	}

}
