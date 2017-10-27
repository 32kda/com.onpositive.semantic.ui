package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Target({ElementType.METHOD}) 
@MetaProperty(key = DefaultMetaKeys.ON_MODIFY_KEY)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnModify
{
	boolean value() default true;
}
