package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.meta.IService;


public interface ICommandExecutor extends IService{

	void execute(ICommand cmd);

}
