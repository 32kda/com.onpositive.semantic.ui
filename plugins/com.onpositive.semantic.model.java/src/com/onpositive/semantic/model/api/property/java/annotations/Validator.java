package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ValidatorAnnotationHandler;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IValidatorProvider;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@CustomMetaHandler(ValidatorAnnotationHandler.class)
public @interface Validator {

	String value() default "";

	@SuppressWarnings("rawtypes")
	Class<? extends IValidator> validatorClass() default IValidator.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IValidatorProvider> validatorProvider() default IValidatorProvider.class;

	String message() default "";
}
