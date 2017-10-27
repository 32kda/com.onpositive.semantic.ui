package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.meta.IService;

public interface IValidationContextProvider extends IService {

	Iterable<IValidationContext> getNestedContexts(IValidationContext context);
	
	IValidationContext getValidationContext(Object object);
	
}
