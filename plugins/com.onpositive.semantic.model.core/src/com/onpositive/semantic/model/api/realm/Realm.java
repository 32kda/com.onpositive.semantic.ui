package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.HashSet;


public class Realm<T> extends CollectionBasedRealm<T> {

	private static final long serialVersionUID = 1L;


	public Realm() {
		super();
	}

	public Realm(Collection<T> elements) {
		super(elements);
	}

	public Realm(T... elements) {
		super(elements);
	}

	
	@Override
	protected Collection<T> createCollection(Collection<T> elements) {
		return new HashSet<T>(elements);
	}



}