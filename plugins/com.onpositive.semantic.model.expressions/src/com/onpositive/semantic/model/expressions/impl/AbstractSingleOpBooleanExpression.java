package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;


public abstract class AbstractSingleOpBooleanExpression extends
		AbstractListenableExpression<Boolean>implements IValueListener<Object>  {

	protected IListenableExpression<?> binding;

	public AbstractSingleOpBooleanExpression(IListenableExpression<?>binding) {
		super();
		this.binding=binding;
		binding.addValueListener(this);
		valueChanged(null, binding.getValue());
	}

	public void dispose() {
		this.binding.removeValueListener(this);
		binding.disposeExpression();
		super.dispose();
	}
	boolean oldValue;
	public void valueChanged(Object oldValue, Object newValue) {
		setNewValue(getValue(newValue));
	}

	protected abstract boolean getValue(Object newValue);
	
}