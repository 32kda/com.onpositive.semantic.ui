package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.core.commands.IHandler;

public interface IAllowsRegisterCommands {

	public Object activateHandler(String str, IHandler handler);

	public void deactivateHandler(Object object);

}
