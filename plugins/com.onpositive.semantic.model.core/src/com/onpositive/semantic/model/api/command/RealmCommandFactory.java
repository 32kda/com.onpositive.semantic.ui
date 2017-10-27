package com.onpositive.semantic.model.api.command;

import java.io.Serializable;


public class RealmCommandFactory extends DefaultCommandFactory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ICommandFactory base;

	public RealmCommandFactory(ICommandFactory commandFactory) {
		this.base = commandFactory;
	}

	@Override
	@SuppressWarnings("rawtypes")
	
	public ICommand createCommand(Object type, Object value, String kind,
			IHasCommandExecutor property) {
		Iterable target = (Iterable) type;
		CompositeCommand c = new CompositeCommand();
		for (Object o : target) {
			ICommand createCommand = base.createCommand(o, value, kind,
					property);
			if (createCommand==null){
				return null;
			}
			c.addCommand(createCommand);
		}
		return c;

	}

}