package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.expressions.IListenableExpression;

public class NotExpression extends AbstractSingleOpBooleanExpression {

	public NotExpression(IListenableExpression<?> binding) {
		super(binding);
		valueChanged(null, binding.getValue());
	}

	@Override
	protected boolean getValue(Object newValue) {
		if (newValue==null){
			return true;
		}
		if (newValue instanceof Boolean){
			return !(Boolean) newValue;
		}
		return true;
	}
	
}
