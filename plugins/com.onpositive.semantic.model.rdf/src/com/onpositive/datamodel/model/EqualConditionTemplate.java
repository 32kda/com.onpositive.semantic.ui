package com.onpositive.datamodel.model;

import java.util.Map;
import java.util.Set;

import com.onpositive.datamodel.impl.IDataStoreRealm;

public class EqualConditionTemplate implements ITemplateCondition{

	private boolean isInverse;
	private IResolvableExpression first;
	private IResolvableExpression next;

	public EqualConditionTemplate(IResolvableExpression first,
			boolean isInverse, IResolvableExpression next) {
		this.first = first;
		this.isInverse = isInverse;
		this.next = next;
	}

	public boolean isConditionMet(IDataStoreRealm realm,
			Map<String, Object> variables) {
		Set<? extends Object> resolve = first.resolve(realm, variables);
		Set<? extends Object> second = next.resolve(realm, variables);
		if (resolve!=null&&second!=null){
			return isInverse?!resolve.equals(second):resolve.equals(second);
		}
		return false;
	}
}
