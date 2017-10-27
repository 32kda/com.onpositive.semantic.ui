package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.binding.Binding;

public class ExternalRealmHandler extends GeneralElementHandler {

	public ExternalRealmHandler()
	{
		super(null,null) ;		
	}
	
	protected Object produceNewInstance(Element element, Object parentContext, Context context) {
		
		Binding b = (Binding) parentContext;
		final Element el = element;
		final String ds = el.getAttribute("url"); //$NON-NLS-1$
		final IRealm<?> realm = GlobalAccess.resolve(ds, IRealm.class);
		b.setRealm(realm);
		return null;	
	}
}
