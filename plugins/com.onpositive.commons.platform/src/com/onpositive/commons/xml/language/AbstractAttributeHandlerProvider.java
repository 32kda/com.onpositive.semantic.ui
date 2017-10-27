package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractAttributeHandlerProvider {
	
	public abstract HashMap<String, IAttributeHandler> constructAttributeHandlers( ArrayList<AttributeDefinition> attributes,
																				   HashMap<String, IAttributeHandler> attributesMap ,
																				   Class<?> clazz, 
																				   HashMap<String, Method> childrenHandlingMap ) ;

}
