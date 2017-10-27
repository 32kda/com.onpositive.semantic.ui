package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.onpositive.core.runtime.Platform;

public class MultiElementHandler implements IElementHandler{

	public Object handleElement(Element element, Object parentContext, Context context)
	{
		try {
			Method method = getClass().getMethod(element.getLocalName().replace('-', '_'),Element.class,Object.class,Context.class);
			
			return method.invoke(this, element,parentContext,context);
			
		} catch (Exception e) {
			Platform.log(e);
		} 
		return null;
	}

	public Method getChildSetter(String childName) {
		// TODO Auto-generated method stub
		return null;
	}
}
