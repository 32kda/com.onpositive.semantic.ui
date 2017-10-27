package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.validation.FixedBoundValidator;
import com.onpositive.semantic.model.api.validation.IValidator;

@Retention(RetentionPolicy.RUNTIME)
@MetaProperty(key = DefaultMetaKeys.FIXED_BOUND_KEY)
@ProvidesService(serviceClass=IValidator.class,implClass=FixedBoundValidator.class)
@Inherited
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface FixedBound {

	String value() default "";
}
