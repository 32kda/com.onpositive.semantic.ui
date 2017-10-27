package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.property.java.annotations.meta.RealmAnnotationHandler;
import com.onpositive.semantic.model.api.realm.IRealmProvider;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@CustomMetaHandler(RealmAnnotationHandler.class)
/**
 * determines realm provider
 */
public @interface RealmProvider {

	@SuppressWarnings("rawtypes")
	@ProvidesService(IRealmProvider.class)
	
	Class<? extends IRealmProvider> value() default IRealmProvider.class;
	
	String expression() default "";
		
}
