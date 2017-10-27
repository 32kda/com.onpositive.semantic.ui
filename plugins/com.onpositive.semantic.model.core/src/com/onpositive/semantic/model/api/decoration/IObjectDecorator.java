package com.onpositive.semantic.model.api.decoration;

public interface IObjectDecorator<T> {

	T decorate(DecorationContext parameterObject, T text);
}
