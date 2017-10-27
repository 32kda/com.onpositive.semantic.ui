package com.google.code.twig.translator;

import com.onpositive.semantic.model.api.property.Predicate;

public class NotPredicate extends Predicate{

	Predicate parent;
	
	public NotPredicate(Predicate parent) {
		super();
		this.parent = parent;
	}

	@Override
	public boolean apply(Object obj) {
		return !parent.apply(obj);
	}

}
