package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.ui.generic.widgets.IActionInterceptor;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;

public class InterceptorHandler extends GeneralElementHandler {
	
	public InterceptorHandler() {
		super( null, null );
	}
	
	@Override
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{
		IListElement<?> selector = (IListElement<?>) parentContext;
		selector.addInterceptor( element.getAttribute("kind"), ( IActionInterceptor) context.newInstance(element.getAttribute("class") ) );
		return null;
	}
}
