package com.onpositive.semantic.model.entity.appengine;

import com.onpositive.semantic.model.api.query.memimpl.InMemoryExecutor;

public class AllInOneExecutor extends InMemoryExecutor {

	public AllInOneExecutor(Iterable<Object> space) {
		super(space);
	}

}
