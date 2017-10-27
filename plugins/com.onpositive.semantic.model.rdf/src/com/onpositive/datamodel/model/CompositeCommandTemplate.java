package com.onpositive.datamodel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;

public class CompositeCommandTemplate implements ICommandTemplate, Serializable {

	ArrayList<ICommandTemplate> commands = new ArrayList<ICommandTemplate>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ICommand toCommand(IDataStoreRealm realm,
			Map<String, Object> variables) {
		CompositeCommand ca = new CompositeCommand();
		for (ICommandTemplate t : commands) {
			ICommand command = t.toCommand(realm, variables);
			if (command != null) {
				ca.addCommand(command);
			}
		}
		if (ca.isEmpty()){
			return null;
		}
		return ca;
	}

}