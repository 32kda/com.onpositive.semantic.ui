package com.onpositive.commons.xml.language;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HandlesAttributeDirectly {

	String value();
}
