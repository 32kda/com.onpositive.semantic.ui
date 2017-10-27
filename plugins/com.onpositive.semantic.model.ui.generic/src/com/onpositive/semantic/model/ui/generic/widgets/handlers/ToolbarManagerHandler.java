package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class ToolbarManagerHandler extends GeneralElementHandler {

	public ToolbarManagerHandler() {
		super( null, null );
	}
	
	
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{		
		final IUIElement<?> mn = (IUIElement<?>) parentContext;
		final IProvidesToolbarManager service = mn.getService( IProvidesToolbarManager.class );
		return service;		
	}
	
	
	protected Object returnedObject( Object newInstance ){
		return null ;
	}
}
