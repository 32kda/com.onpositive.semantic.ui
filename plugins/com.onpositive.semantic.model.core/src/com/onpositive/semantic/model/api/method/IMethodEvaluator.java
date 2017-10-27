package com.onpositive.semantic.model.api.method;

import com.onpositive.semantic.model.api.meta.IHasMeta;

/**
 * Basic method evaluator abstraction
 * @author 32kda
 */
public interface IMethodEvaluator extends IHasMeta {
	Object evaluateCall(Object baseObject, Object[] params);
	String getMethodName();
}
