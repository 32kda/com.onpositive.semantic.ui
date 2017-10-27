package com.google.code.twig.util;

import com.google.code.twig.Restriction;

public class RestrictionToPredicateAdaptor<T> extends com.onpositive.semantic.model.api.property.Predicate<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Restriction<T> restriction;
	
	public RestrictionToPredicateAdaptor(Restriction<T> restriction)
	{
		this.restriction = restriction;
	}
		
	public boolean apply(T input)
	{
		return restriction.allow(input);
	}
}
