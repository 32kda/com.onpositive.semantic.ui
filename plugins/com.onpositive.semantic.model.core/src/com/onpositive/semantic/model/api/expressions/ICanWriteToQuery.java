package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.query.Query;

public interface ICanWriteToQuery {

	boolean modify(Query q);
}
