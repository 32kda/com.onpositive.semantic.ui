package com.onpositive.semantic.model.api.property;

public abstract class Predicate<T> extends ComputedProperty{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Predicate() {
		super("","",Boolean.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Object obj) {
		return apply((T) obj);
	}

	public abstract boolean apply(T obj) ;

}
