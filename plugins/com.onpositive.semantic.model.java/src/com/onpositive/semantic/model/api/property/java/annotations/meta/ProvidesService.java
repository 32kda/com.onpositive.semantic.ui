package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidesService {

	Class<?>serviceClass() default Object.class;
	
	Class<?>implClass() default Object.class;
	
	Class<?>value()default Object.class;
	
	boolean passParameters() default true;
}
