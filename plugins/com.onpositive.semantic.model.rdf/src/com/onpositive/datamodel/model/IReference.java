package com.onpositive.datamodel.model;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.IDataStoreRealm;

public interface IReference extends IResolvableExpression{

	public IEntry resolve(IDataStoreRealm realm);
}
