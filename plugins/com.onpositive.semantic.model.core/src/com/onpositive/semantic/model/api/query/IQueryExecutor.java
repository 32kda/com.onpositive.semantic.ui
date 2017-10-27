package com.onpositive.semantic.model.api.query;

import java.io.Serializable;

import com.onpositive.semantic.model.api.realm.IResultUpdate;

public interface IQueryExecutor extends Serializable{

	QueryResult execute(Query query, IResultUpdate async);
	
	void cancel(IResultUpdate async);
}
