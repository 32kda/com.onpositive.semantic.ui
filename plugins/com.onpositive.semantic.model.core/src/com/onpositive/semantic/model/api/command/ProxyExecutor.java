package com.onpositive.semantic.model.api.command;

import java.io.Serializable;

import com.onpositive.semantic.model.api.globals.GlobalAccess;

public class ProxyExecutor implements IHasCommandExecutor,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	

	public ProxyExecutor(String string) {
		this.id=string;
	}

	@Override
	public ICommandExecutor getCommandExecutor() {
		IHasCommandExecutor global = getHasCommandExecutor();
		return global.getCommandExecutor();
	}

	public IHasCommandExecutor getHasCommandExecutor() {
		IHasCommandExecutor global = (IHasCommandExecutor) GlobalAccess.getGlobal(GlobalAccess.stringToKey(id));
		return global;
	}

	@Override
	public ICommandFactory getCommandFactory() {
		IHasCommandExecutor global = getHasCommandExecutor();
		return global.getCommandFactory();
	}


}
