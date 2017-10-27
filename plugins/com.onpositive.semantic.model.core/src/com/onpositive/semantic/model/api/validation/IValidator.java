package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.meta.IService;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public interface IValidator<T> extends IService{

	public CodeAndMessage isValid(IValidationContext context, T value);

//	public CodeAndMessage isValid(IValidationContext context, Collection<T> object);

}
