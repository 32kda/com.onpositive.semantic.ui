package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Target({ElementType.FIELD, ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable
{
	@MetaProperty(key = DefaultMetaKeys.SEARCHABLE_KEY)
	boolean value() default true;
}
