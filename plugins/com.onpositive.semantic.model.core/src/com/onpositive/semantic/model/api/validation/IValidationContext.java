package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.meta.IContext;

public interface IValidationContext extends IContext {

	Iterable<IValidationContext> getNestedContexts();
	
	IValidationContext getParent();
	
	public static final String DEEP_VALIDATION ="deep_validation";
	public static final String NEVER_VALIDATE ="never_validate";

	Object getUnconvertedValue();

	Iterable<Object> getRealm();

		
	
}
