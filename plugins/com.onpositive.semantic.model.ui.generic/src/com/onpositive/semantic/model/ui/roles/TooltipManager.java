package com.onpositive.semantic.model.ui.roles;


public class TooltipManager extends AbstractRoleMap<TooltipObject> {

	private TooltipManager() {
		super("com.onpositive.semantic.model.tooltip", TooltipObject.class); //$NON-NLS-1$
	}

	private static TooltipManager instance;

	public static TooltipManager getInstance() {
		if (instance == null) {
			instance = new TooltipManager();
		}
		return instance;
	}

	public TooltipObject getTooltipObject(Object object, String role,
			String theme) {
		this.checkLoad();
		if (object==null){
			return null;
		}
		final Class<?> class1 = object.getClass();
		final RoleKey ks = new RoleKey(this.getName(class1), role, theme, this.getTypes(null));
		return this.getObject(class1, ks, null);
	}
}
