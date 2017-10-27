package com.onpositive.semantic.model.binding;

import java.io.Serializable;

import com.onpositive.semantic.model.api.command.ICommand;


public interface ICommitListener extends Serializable {

	public void commitPerformed(ICommand command);
}
