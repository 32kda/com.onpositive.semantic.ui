package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.validation.IValidationContext;

@Retention(RetentionPolicy.RUNTIME)
@MetaProperty(key=IValidationContext.DEEP_VALIDATION,boolValue=true)
public @interface NestedValidation {

}
