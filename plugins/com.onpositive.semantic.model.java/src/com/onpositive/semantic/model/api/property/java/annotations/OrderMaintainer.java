package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.order.IOrderMaintainer;

@Retention(RetentionPolicy.RUNTIME)
//TODO FIX ME
public @interface OrderMaintainer {

	Class<? extends IOrderMaintainer>value();
}
