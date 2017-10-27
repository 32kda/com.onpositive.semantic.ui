package com.onpositive.cc;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;

public class SS implements IElementHandler{

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		System.out.println("Hello world");
		return null;
	}

	public Method getChildSetter(String childName) {
		// TODO Auto-generated method stub
		return null;
	}

}
