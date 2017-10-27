package com.onpositive.semantic.model.api.changes;

import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;

public interface IRoledListener extends IObjectListener{

	public final String ADD=SimpleOneArgCommand.ADD;
	public final String DELETE=SimpleOneArgCommand.DELETE;
	public final String UPDATE=SimpleOneArgCommand.SET_VALUE;
	
	String getRole();
}
