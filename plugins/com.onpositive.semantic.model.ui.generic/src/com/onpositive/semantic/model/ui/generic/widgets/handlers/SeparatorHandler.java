package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.actions.Separator;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class SeparatorHandler extends AbstractActionElementHandler {

	public SeparatorHandler() {
		// TODO Auto-generated constructor stub
	}
	
	protected Separator contribute(ActionsSetting parentContext, Context context, Element element)
	{
		Separator s=new Separator();
		//handleAction(element, parentContext, s, parentContext.gui);
		//super.contribute(parentContext, context, element);
		//parentContext.addAction(m, element);
		return s;
	}

}
