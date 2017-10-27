package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.query.IQueryPreProcessor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;

public class TestPreprocessor implements IQueryPreProcessor{

	@Override
	public Query preProcess(Query query) {
		query.addFilter(new QueryFilter("a", null, null));
		return query;
	}

}
