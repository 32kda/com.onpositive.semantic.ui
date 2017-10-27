package com.onpositive.commons.xml.language;

import com.onpositive.core.runtime.Bundle;

public class BasicHandlerReference extends ObjectReference
{
	Class<?> inputClass ;
	String inputClassName ;

	public BasicHandlerReference(Bundle bundleContext, String className) {
		//TODO set bundle context
		super( bundleContext, className );
		this.object = new GeneralElementHandler( bundleContext, className ) ;
		this.inputClass = GeneralElementHandler.class ;
	}

}
