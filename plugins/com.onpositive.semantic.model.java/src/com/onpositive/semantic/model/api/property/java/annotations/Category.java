package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * This annotation should be used to provide general human readable name for the meta instance,
 * possible targets are: class,field,method
 */
public @interface Category {
	@MetaProperty(key=DefaultMetaKeys.CATEGORY_KEY)
	String value();
}
