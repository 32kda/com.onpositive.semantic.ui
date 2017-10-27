package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.property.java.annotations.meta.CommutativeHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD,ElementType.FIELD})
@CustomMetaHandler(CommutativeHandler.class)
public @interface CommutativeWith {
	String value();
}
