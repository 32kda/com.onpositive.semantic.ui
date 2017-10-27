package com.onpositive.semantic.model.api.command;

/**
 * Command processor is applyed to commands before they are executed
 * and is free to modify command or throw command excetion to cancel it is execution;
 * 
 * @author kor
 *
 */
public interface ICommandPreProcessor {


	/**
	 * 
	 * @param cmd
	 * @return modified command or null if no modification required
	 * @throws CommandException
	 */
	ICommand preProcess(ICommand cmd) throws CommandException;
}
