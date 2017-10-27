package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.annotation.Annotation;

import com.onpositive.semantic.model.api.meta.IWritableMeta;

public interface CustomHandler<A extends Annotation> {

	void handle(A annotation,IWritableMeta meta);
}
