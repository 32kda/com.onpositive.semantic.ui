package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class ValidatorProviderValidator<T> implements IValidator<T>{

	private final IValidatorProvider<T>provider;
	
	
	public ValidatorProviderValidator(IValidatorProvider<T> provider) {
		super();
		this.provider = provider;
	}

	@Override
	public CodeAndMessage isValid(IValidationContext context, T object) {
		IValidator<T> validator = provider.getService(object);
		if (validator!=null){
			return validator.isValid(context, object);
		}
		return null;
	}
}
