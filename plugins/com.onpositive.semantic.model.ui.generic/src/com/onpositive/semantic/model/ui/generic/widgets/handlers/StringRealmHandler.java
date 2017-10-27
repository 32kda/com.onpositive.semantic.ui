package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;

public class StringRealmHandler implements IElementHandler{

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		Binding bnd=(Binding) parentContext;
		Realm<String> ss=new Realm<String>();
		bnd.setRealm(ss);
		DOMEvaluator.evaluateChildren(element, ss, context);
		return ss;
	}

}
