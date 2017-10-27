package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.xmlnew.BindingElementHandler;

public class ObjectValueHandler extends BindingElementHandler {
	
	
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{
		String attribute = element.getAttribute( "class" ) ;
		final Object newInstance = context.newInstance( attribute ) ;

		if (newInstance != null)
		{			
			Binding newBinding=new Binding( newInstance ) ;			
			newBinding.setValue( newInstance, null ) ;
			newBinding.setReadOnly(false) ;
			return newBinding ;
		}
		return null ;
	}
}
