package com.onpositive.datamodel.model;

import java.util.Map;

import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.command.ICommand;

public interface ICommandTemplate {

	ICommand toCommand(IDataStoreRealm realm,Map<String,Object> variables);
	
}
