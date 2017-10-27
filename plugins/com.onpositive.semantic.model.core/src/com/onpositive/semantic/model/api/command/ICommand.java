package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface ICommand extends IHasMeta{

	
	public static final String REMOVE_VALUE = "remove";
	public static final String ADD_VALUE = "add_value";
	
	public static final String UP_VALUE = "up_value";
	public static final String DOWN_VALUE = "down_value";
	
	public static final String SET_VALUE = "set_value";
	public static final String SET_VALUES = SET_VALUE;

	public static final String COMPOSITE = "composite";

	public static final String ADD = ADD_VALUE;
	public static final String DELETE = REMOVE_VALUE;
	
	String getKind();

	public static final String META_PROPERTY_SILENTLY="DO_NOT_FIRE_CHANGES";
	public static final String META_PROPERTY_ASYNC_CHANGES="CHANGES_ASYNC";
	
	ICommandExecutor getCommandExecutor();
	IHasCommandExecutor getOwner();
}
