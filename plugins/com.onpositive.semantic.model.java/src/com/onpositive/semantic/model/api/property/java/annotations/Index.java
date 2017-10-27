package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Target({ElementType.FIELD, ElementType.TYPE}) 
@MetaProperty(key = DefaultMetaKeys.INDEXABLE_KEY)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index
{
	boolean value() default true;
}
