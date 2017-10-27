package com.onpositive.datamodel.model;

import java.util.Map;

import com.onpositive.datamodel.impl.IDataStoreRealm;

public interface ITemplateCondition {

	public boolean isConditionMet(IDataStoreRealm realm,
			Map<String, Object> variables);
}
