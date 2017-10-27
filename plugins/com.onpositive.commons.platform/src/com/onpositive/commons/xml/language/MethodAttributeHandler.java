package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;



public class MethodAttributeHandler<T> extends AttributeHandler<T>{
	
	protected Method method;
	
	public MethodAttributeHandler(Class<T> clazz,Method m) {
		super(clazz);
		try {
			method=m;
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public
	void handle(T element, Object pContext, String value, Context ctx) {
		try {
			method.invoke(element, convert(value));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected Object convert(String value) {
		return value;
	}
}
