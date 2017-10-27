package com.onpositive.semantic.model.api.validation;



public interface IValidatorProvider<T> {

	IValidator<T> getService(Object object);
}
