package com.onpositive.semantic.model.ui.roles;

import com.onpositive.commons.platform.registry.RegistryMap;

public class RoleManager extends RegistryMap<SemanticRole> {

	private RoleManager() {
		super("com.onpositive.semantic.ui.role", SemanticRole.class); //$NON-NLS-1$
	}

	private static RoleManager instance;

	public static RoleManager getInstance() {
		if (instance == null) {
			instance = new RoleManager();
		}
		return instance;
	}

	public static final String ADD_ROLE = "add"; //$NON-NLS-1$
	public static final String EDIT_ROLE = "edit"; //$NON-NLS-1$
	public static final String NEW_ROLE = "new"; //$NON-NLS-1$
	public static final String ROW_ROLE = "row"; //$NON-NLS-1$
	public static final String CELL_ROLE = "cell"; //$NON-NLS-1$

}