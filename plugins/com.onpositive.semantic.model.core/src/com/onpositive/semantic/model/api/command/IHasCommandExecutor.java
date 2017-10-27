package com.onpositive.semantic.model.api.command;

import java.io.Serializable;

public interface IHasCommandExecutor extends Serializable{

	ICommandExecutor getCommandExecutor();

	ICommandFactory getCommandFactory();
}
