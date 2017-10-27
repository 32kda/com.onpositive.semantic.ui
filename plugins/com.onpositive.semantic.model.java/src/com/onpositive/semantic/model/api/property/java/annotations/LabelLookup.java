package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * Provides label lookup service
 */
public @interface LabelLookup {

	@ProvidesService(ILabelLookup.class)
	Class<? extends ILabelLookup> value() default ILabelLookup.class;
}
