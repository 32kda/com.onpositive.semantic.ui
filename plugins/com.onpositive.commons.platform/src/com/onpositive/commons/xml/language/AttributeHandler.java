package com.onpositive.commons.xml.language;


public abstract class AttributeHandler<T> {

	private final Class<T> clazz;

	public AttributeHandler(Class<T> clazz) {
		this.clazz = clazz;
	}

	public abstract void handle(T element, Object pContext, String value,
			Context ctx);

	public boolean isApplyable(T element) {
		return this.clazz.isInstance(element);
	}
}