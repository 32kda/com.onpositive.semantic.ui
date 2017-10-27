package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface ITargetDependentReadonly {

	public boolean isReadonly(IHasMeta meta,Object object);
	
	IListenableExpression<?>buildReadonlyExpression(IHasMeta meta, IExpressionEnvironment env);
}
