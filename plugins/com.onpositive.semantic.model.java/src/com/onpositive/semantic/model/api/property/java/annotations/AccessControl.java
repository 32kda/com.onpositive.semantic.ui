package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.query.IPermissionManager;

@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {

	String controllerId();

	Class<? extends IPermissionManager> manager();
}
