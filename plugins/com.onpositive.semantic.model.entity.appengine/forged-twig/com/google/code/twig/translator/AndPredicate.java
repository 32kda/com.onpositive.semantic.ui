package com.google.code.twig.translator;

import com.onpositive.semantic.model.api.property.Predicate;

public class AndPredicate extends Predicate{

	Predicate p1;
	public AndPredicate(Predicate p1, Predicate p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	Predicate p2;
	
	@Override
	public boolean apply(Object obj) {
		return p1.apply(obj)&&p2.apply(obj);
	}

}
