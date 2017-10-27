package com.onpositive.semantic.model.api.command;

import java.io.Serializable;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;


public class DefaultCommandFactory implements ICommandFactory,IHasMeta,Serializable {

	private static final long serialVersionUID = 1L;
	public static final ICommandFactory INSTANCE = new DefaultCommandFactory();


	@Override
	public ICommand createCommand(Object type, Object value, String kind,
			IHasCommandExecutor property) {
		return new SimpleOneArgCommand(type, value, kind, property);
	}

	@Override
	public final ICommand createRemoveValueCommand(IHasCommandExecutor property, Object type,
			Object value) {
		return createCommand(type, value, ICommand.REMOVE_VALUE,
				property);
	}

	@Override
	public final ICommand createAddValueCommand(IHasCommandExecutor property, Object type,
			Object value) {
		return createCommand(type, value, ICommand.ADD_VALUE,
				property);
	}

	@Override
	public final ICommand createSetValueCommand(IHasCommandExecutor property, Object type,
			Object value) {
		return createCommand(type, value, ICommand.SET_VALUE,
				property);
	}
	
	
	public final ICommand createUpValueCommand(IHasCommandExecutor property, Object type,
			Object value) {
		return createCommand(type, value, ICommand.UP_VALUE,
				property);
	}
	public final ICommand createDownValueCommand(IHasCommandExecutor property, Object type,
			Object value) {
		return createCommand(type, value, ICommand.DOWN_VALUE,
				property);
	}

	@Override
	public final ICommand createSetValuesCommand(IHasCommandExecutor property, Object type,
			Object... values) {
		return createCommand(type, values, ICommand.SET_VALUES,
				property);
	}
	

	
	@Override
	public IMeta getMeta() {
		return null;
	}

	
}
