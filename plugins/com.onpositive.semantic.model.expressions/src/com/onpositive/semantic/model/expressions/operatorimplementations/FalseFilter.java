package com.onpositive.semantic.model.expressions.operatorimplementations;

import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;

public class FalseFilter extends AbstractFilter implements IDescribableToQuery{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean accept(Object element) {
		return false;
	}

	@Override
	public boolean adapt(Query query) {
		query.setNoResults(true);
		return true;
	}

}
