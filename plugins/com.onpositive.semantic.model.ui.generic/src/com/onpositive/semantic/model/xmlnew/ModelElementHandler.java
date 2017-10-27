package com.onpositive.semantic.model.xmlnew;
import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class ModelElementHandler extends BindingElementHandler {

	public ModelElementHandler() {
		super() ;
	}
	
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{
		IBindable iBindable = (IBindable) parentContext;
		Binding binding = ( Binding ) iBindable.getBinding();
		if (binding==null){
			binding=new Binding("");
			iBindable.setBinding(binding);
		}
		if ( parentContext instanceof IUIElement )
			binding.setAdapter( IUIElement.class, parentContext ) ;
		
		return binding ;
	}
	
	
	protected Object returnedObject( Object newInstance ){
		return null ;
	}
}
