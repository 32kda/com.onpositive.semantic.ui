package com.onpositive.semantic.model.xmlnew;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.binding.Binding;

public class BindingElementHandler extends GeneralElementHandler{
	
	public BindingElementHandler() {
		super( null , null ) ;
		setObjectClass( Binding.class ) ;
	}
	
	
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{
		String attribute = element.getAttribute("path");
		if (attribute.length()==0){
			attribute=element.getAttribute("id");
		}
		Binding result = ( (Binding)parentContext ).binding( attribute ) ;
		return  result ;
	}
}