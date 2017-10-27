package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.java.annotations.meta.CustomMetaHandler;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.property.java.annotations.meta.TextLabelAnnotationHandler;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@CustomMetaHandler(TextLabelAnnotationHandler.class)
public @interface TextLabel {
	
	String value() default "";
	String description() default "";
	
	
	boolean useWithNull() default false;
	
	@ProvidesService(ITextLabelProvider.class)
	Class<? extends ITextLabelProvider> provider() default ITextLabelProvider.class;
}
