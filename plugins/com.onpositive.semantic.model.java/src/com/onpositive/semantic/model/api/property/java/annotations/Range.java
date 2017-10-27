package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
//TODO FIX ME
//@ProvidesService(serviceClass=IValidator.class,implClass=R.class)
public @interface Range {

	@MetaProperty(key = DefaultMetaKeys.RANGE_MIN__KEY)
	double min() default Double.MIN_VALUE;

	@MetaProperty(key = DefaultMetaKeys.RANGE_MAX__KEY)
	double max() default Double.MAX_VALUE;

	@MetaProperty(key = DefaultMetaKeys.RANGE_DIGITS__KEY)
	int digits() default 0;

	@MetaProperty(key = DefaultMetaKeys.RANGE_INCREMENT__KEY)
	double increment() default 1;

	@MetaProperty(key = DefaultMetaKeys.RANGE_PAGE_INCREMENT__KEY)
	double pageIncrement() default 1;
}
