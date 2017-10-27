package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.Activator;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.core.runtime.Bundle;
public class ProviderAndDecoratorHandler extends GeneralElementHandler {

	public ProviderAndDecoratorHandler() {
		super( null, null );
	}
	
	@Override
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{
		Object newInstance = null ;
		try{
			String arg = element.getAttribute("name").equals("decorator") ? element.getAttribute("value") :  element.getAttribute("class") ;
			newInstance = context.newInstance( arg );
		}catch (Exception e) {
			Activator.log(e);
		}
		return newInstance;
	}	
}
