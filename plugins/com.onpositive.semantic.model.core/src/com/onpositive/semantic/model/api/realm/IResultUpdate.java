package com.onpositive.semantic.model.api.realm;

import java.io.Serializable;

import com.onpositive.semantic.model.api.query.QueryResult;

public interface IResultUpdate extends Serializable{

	void fetched(QueryResult status);	
}
