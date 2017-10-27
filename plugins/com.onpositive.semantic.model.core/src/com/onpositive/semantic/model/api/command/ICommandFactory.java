package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.meta.IService;

public interface ICommandFactory extends IService {

	ICommand createCommand(Object type, Object value, String kind,
			IHasCommandExecutor property);

	ICommand createSetValueCommand(IHasCommandExecutor property, Object object,
			Object value);

	ICommand createAddValueCommand(IHasCommandExecutor property, Object object,
			Object value);

	ICommand createRemoveValueCommand(IHasCommandExecutor property,
			Object object, Object value);

	ICommand createSetValuesCommand(IHasCommandExecutor property,
			Object object, Object... values);
	
	 ICommand createUpValueCommand(IHasCommandExecutor property, Object type,
			Object value);
	ICommand createDownValueCommand(IHasCommandExecutor property, Object type,
			Object value) ;

}
