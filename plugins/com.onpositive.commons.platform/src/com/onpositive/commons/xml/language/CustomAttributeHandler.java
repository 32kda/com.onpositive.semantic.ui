package com.onpositive.commons.xml.language;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAttributeHandler {

	String value();
	Class<? extends AbstractContextDependentAttributeHandler>handler();
}
