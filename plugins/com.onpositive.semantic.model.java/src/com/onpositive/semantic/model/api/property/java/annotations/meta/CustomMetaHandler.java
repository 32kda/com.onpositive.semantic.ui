package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMetaHandler {

	Class<? extends CustomHandler<?>>value();
}
