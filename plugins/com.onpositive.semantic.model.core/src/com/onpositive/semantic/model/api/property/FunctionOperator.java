package com.onpositive.semantic.model.api.property;

public abstract class FunctionOperator implements IBinaryOperator{

	@Override
	public Object getValue(Object o1, Object o2) {
		IFunction f=(IFunction) o1;
		return calc(f,o2);
	}

	protected abstract Object calc(IFunction f, Object o2) ;

}
