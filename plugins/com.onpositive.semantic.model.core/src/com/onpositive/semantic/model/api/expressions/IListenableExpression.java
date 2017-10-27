package com.onpositive.semantic.model.api.expressions;

import java.io.Serializable;

import com.onpositive.semantic.model.api.changes.IValueListener;


public interface IListenableExpression<T> extends Serializable {

	void addValueListener(IValueListener<?> exp);
	
	void removeValueListener(IValueListener<?> exp);

	Object getValue();

	void disposeExpression();

	public String getMessage();

}
