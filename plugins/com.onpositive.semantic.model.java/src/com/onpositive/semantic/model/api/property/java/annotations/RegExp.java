package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.RegexpValidator;

@Retention(RetentionPolicy.RUNTIME)
@ProvidesService(serviceClass=IValidator.class,implClass=RegexpValidator.class)
public @interface RegExp {

	String value();

	String message() default "";
}
