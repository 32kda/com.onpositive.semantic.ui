package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;

public interface IElementHandlerWithValidation extends IElementHandler {
	
	Method getChildSetter(String childName);
}
