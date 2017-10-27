package com.onpositive.semantic.model.ui.roles;

import java.util.Set;


public class ContentAssistManager extends AbstractRoleMap<ContentAssistObject> {

	private ContentAssistManager() {
		super(
				"com.onpositive.semantic.model.contentAssist", ContentAssistObject.class); //$NON-NLS-1$
	}

	private static ContentAssistManager instance;

	public static ContentAssistManager getInstance() {
		if (instance == null) {
			instance = new ContentAssistManager();
		}
		return instance;
	}

	public ContentAssistObject getContentAssistObject(Object object,
			String role, String theme) {
		this.checkLoad();
		final Class<?> class1 = object.getClass();

		Set<? extends Object> types = null;		
		final RoleKey ks = new RoleKey(this.getName(class1), role, theme, this.getTypes(types));
		return this.getObject(class1, ks, types);
	}
}
