package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class CommandException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final CodeAndMessage status;
	
	private final ICommand command;

	public CommandException(CodeAndMessage status, ICommand command) {
		super();
		this.status = status;
		this.command = command;
	}

	public CodeAndMessage getStatus() {
		return status;
	}

	public ICommand getCommand() {
		return command;
	}
}
