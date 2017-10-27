package com.onpositive.semantic.ui.layouts;

import com.onpositive.semantic.model.api.roles.AbstractRoleMap;

public class SemanticLayoutRegistry extends AbstractRoleMap<SemanticLayout> {

	public SemanticLayoutRegistry() {
		super(
				"com.onpositive.semantic.model.semanticLayout", SemanticLayoutRegistry.class); //$NON-NLS-1$
	}

}
