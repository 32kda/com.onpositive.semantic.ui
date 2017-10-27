package com.onpositive.semantic.model.api.expressions;

import java.io.Serializable;

import com.onpositive.semantic.model.api.access.IClassResolver;




public interface IExpressionEnvironment extends Serializable{

	IListenableExpression<?> getBinding(String path);
	
	IClassResolver getClassResolver();
}
