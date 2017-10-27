package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MetaProperty {

	String key();

	String value() default "";

	Class<? extends Object> classValue() default Object.class;
	
	boolean passParameters() default true;
	
	boolean createInstance() default true;	

	boolean boolValue() default true;
}
