package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Retention(RetentionPolicy.RUNTIME)
@MetaProperty(key = DefaultMetaKeys.ID_KEY)
@Unique
@Inherited
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface Id {

}
