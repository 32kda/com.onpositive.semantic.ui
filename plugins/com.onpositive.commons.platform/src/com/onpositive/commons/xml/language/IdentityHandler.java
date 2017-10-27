package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

public class IdentityHandler implements IElementHandler{

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		DOMEvaluator.evaluateChildren(element, parentContext, context);
		return parentContext;
	}

	public Method getChildSetter(String childName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
