package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.factory.IFactoryProvider;
import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.FactoryAnnotationHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@CustomMetaHandler(FactoryAnnotationHandler.class)
public @interface FactoryProvider {

	@ProvidesService(IFactoryProvider.class)
	Class<? extends IFactoryProvider> value() default IFactoryProvider.class;
	
	String expression() default "";
	
	String caption() default "...";
}
