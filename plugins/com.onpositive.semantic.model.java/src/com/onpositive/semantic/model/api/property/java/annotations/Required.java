package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.RequiredValidator;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@MetaProperty(key = DefaultMetaKeys.REQUIRED_KEY)
@ProvidesService(serviceClass=IValidator.class,implClass=RequiredValidator.class)
public @interface Required {

	String expression() default "";
	
	@MetaProperty(key = DefaultMetaKeys.REQUIRED_KEY
			+ DefaultMetaKeys.DESCRIPTION_SUFFIX)
	String value() default "";
	//TODO AND BINDINGS???
	
}
