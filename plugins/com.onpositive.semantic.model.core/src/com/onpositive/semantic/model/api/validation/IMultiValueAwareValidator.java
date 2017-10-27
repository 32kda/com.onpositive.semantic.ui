package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public interface IMultiValueAwareValidator {

	public CodeAndMessage isValid(IValidationContext context, Iterable<Object> object) ;
}
