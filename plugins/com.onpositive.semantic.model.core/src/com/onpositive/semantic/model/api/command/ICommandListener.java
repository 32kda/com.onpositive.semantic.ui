package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.changes.IObjectListener;

public interface ICommandListener extends IObjectListener{

	void commandExecuted(ICommand cm);
}
