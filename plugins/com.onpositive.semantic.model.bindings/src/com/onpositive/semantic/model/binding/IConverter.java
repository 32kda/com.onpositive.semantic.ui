package com.onpositive.semantic.model.binding;

import com.onpositive.semantic.model.api.meta.IService;

public interface IConverter<A,B> extends IService{

	B from(A source);
	
	A to(B source);
}
