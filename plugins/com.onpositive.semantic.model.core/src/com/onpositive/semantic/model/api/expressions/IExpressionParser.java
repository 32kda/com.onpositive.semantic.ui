package com.onpositive.semantic.model.api.expressions;

public interface IExpressionParser {

	IListenableExpression<Object> parse(String expresssion,
			IExpressionEnvironment env);
}
