package com.onpositive.semantic.model.api.expressions;


public interface ISubsitutableExpression<T> extends IListenableExpression<T>{

	ISubsitutableExpression<T> substituteAllExcept(IListenableExpression<?> ve);

	public boolean isConstant();
}
