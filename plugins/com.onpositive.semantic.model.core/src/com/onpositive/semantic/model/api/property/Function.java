package com.onpositive.semantic.model.api.property;

public abstract class Function<A,T> extends ComputedProperty{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Function() {
		super("","",Object.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Object obj) {
		return apply((A) obj);
	}

	public abstract T apply(A obj) ;

}
