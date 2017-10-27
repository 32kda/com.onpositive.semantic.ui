package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.api.validation.ExpressionBasedValidator;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IValidatorProvider;
import com.onpositive.semantic.model.api.validation.ValidatorProviderValidator;

@SuppressWarnings({ "rawtypes" })
public class ValidatorAnnotationHandler implements CustomHandler<Validator> {

	
	@SuppressWarnings("unchecked")
	
	public void handle(Validator annotation, IWritableMeta meta) {		
		Class<? extends IValidator> validatorClass = annotation
				.validatorClass();
		try {
			if (validatorClass != IValidator.class) {
				meta.registerService(IValidator.class,
						validatorClass.newInstance());
			}
			Class<? extends IValidatorProvider> validatorProvider = annotation
					.validatorProvider();
			if (validatorProvider != IValidatorProvider.class) {
				meta.registerService(IValidator.class,
						new ValidatorProviderValidator(validatorProvider.newInstance()));
			}
			String expr = annotation.value();
			if (expr.length() > 0) {
				meta.registerService(
						IValidator.class,
						new ExpressionBasedValidator(expr, annotation.message()));
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
