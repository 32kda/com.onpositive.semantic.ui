package com.onpositive.semantic.model.api.query;

import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.meta.IMeta;


public interface ITransport extends ICommandExecutor{

	QueryResult executeQuery(Query query);

	Object get(Object id, IMeta meta);

	Object getObject(Object id);
	
}
