package com.onpositive.semantic.model.api.query;

import java.io.Serializable;

import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public interface IPermissionManager extends Serializable{

	String AUTHENTIFICATED="authentificated";
	String ADMIN="admin";
	
	CodeAndMessage validateQuery(Query q);

	QueryResult adjustResults(QueryResult r, Query q);
	
	CodeAndMessage validateCommand(ICommand c);
	
	
}
