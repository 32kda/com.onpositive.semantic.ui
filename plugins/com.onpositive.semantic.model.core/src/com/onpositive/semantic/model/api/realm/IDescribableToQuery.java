package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.query.Query;

public interface IDescribableToQuery {

	boolean adapt(Query query);

	
}
