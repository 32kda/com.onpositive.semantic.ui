package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.api.realm.Realm;

public class StringValueHandler implements IElementHandler{

	@SuppressWarnings("unchecked")
	public Object handleElement(Element element, Object parentContext, Context context)
	{
		Realm<Object> lm=(Realm<Object>) parentContext;
		lm.add(element.getAttribute("value"));
		return null;
	}

}
