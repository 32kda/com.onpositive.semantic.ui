package com.onpositive.semantic.model.entity.appengine;

import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.memimpl.PartialInMemoryExecutor;

public abstract class AbstractAppEngineExecutor extends PartialInMemoryExecutor{

	public AbstractAppEngineExecutor(IQueryExecutor executor) {
		super(executor);
	}

}
