package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ITargetDependentReadonly;
import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ReadOnlyAnnotationHandler;

@Retention(RetentionPolicy.RUNTIME)
@MetaProperty(key = DefaultMetaKeys.READ_ONLY_KEY)
@CustomMetaHandler(ReadOnlyAnnotationHandler.class)
public @interface Enablement {

	String value() default "";
	
	String message() default "";

	Class<? extends ITargetDependentReadonly> readOnlyProvider() default ITargetDependentReadonly.class;
}
