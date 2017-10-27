package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * This annotation should be used to provide id of icon used for displaying annotated field
 * Value should be either string id of registered image extension or path to image 
 */
public @interface Image {
	@MetaProperty(key=DefaultMetaKeys.IMAGE_KEY)
	String value();
}
