package com.onpositive.semantic.model.ui.roles;

public class ObjectDecoratorManager extends
		GatheringRoleMap<ObjectDecoratorDescriptor> {

	private ObjectDecoratorManager() {
		super(
				"com.onpositive.semantic.model.decorators", ObjectDecoratorDescriptor.class); //$NON-NLS-1$
	}

	private static ObjectDecoratorManager instance;

	public static ObjectDecoratorManager getInstance() {
		if (instance == null) {
			instance = new ObjectDecoratorManager();
		}
		return instance;
	}
}
