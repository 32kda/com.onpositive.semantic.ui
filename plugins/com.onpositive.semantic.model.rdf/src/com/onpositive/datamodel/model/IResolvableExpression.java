package com.onpositive.datamodel.model;

import java.util.Map;
import java.util.Set;

import com.onpositive.datamodel.impl.IDataStoreRealm;

public interface IResolvableExpression {

	Set<? extends Object>resolve(IDataStoreRealm realm,Map<String,Object>parameters);
	
	boolean isResolvable(IDataStoreRealm ra,Map<String,Object> parameters);
}
