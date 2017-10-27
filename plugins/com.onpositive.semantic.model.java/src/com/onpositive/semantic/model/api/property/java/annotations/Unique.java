package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.UniqueValidator;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@MetaProperty(key = DefaultMetaKeys.UNIQUE_KEY)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@ProvidesService(serviceClass=IValidator.class,implClass=UniqueValidator.class)
public @interface Unique {

}
