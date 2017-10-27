package com.onpositive.datamodel.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.onpositive.datamodel.impl.IDataStoreRealm;

public class Literal implements IResolvableExpression,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object object; 
	
	public Literal(Object object) {
		super();
		this.object = object;
	}

	public boolean isResolvable(IDataStoreRealm ra,
			Map<String, Object> parameters) {
		return true;
	}

	public Set<? extends Object> resolve(IDataStoreRealm realm,
			Map<String, Object> parameters) {
		return Collections.singleton(object);
	}
}
