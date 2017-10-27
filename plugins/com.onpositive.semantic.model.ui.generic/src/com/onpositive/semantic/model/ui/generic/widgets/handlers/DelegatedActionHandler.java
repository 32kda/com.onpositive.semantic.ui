package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;


public class DelegatedActionHandler extends AbstractActionElementHandler {

	public DelegatedActionHandler() {
	}
//
//	protected void contribute(final ActionsSetting parentContext,
//			Context context, Element element) {
//		final String attribute = element.getAttribute("class");
//		final ClassLoader classLoader = context.getClassLoader();
//		final BindedAction basicAction = new DelegatedBindedAction(IAction.AS_PUSH_BUTTON, attribute, classLoader, (AbstractUIElement<Control>) parentContext.getControl());
//		UIElementHandler.handleAction(element, parentContext, basicAction,parentContext.getControl());
//		parentContext.addAction(basicAction);
//	}
//
//	
//	@SuppressWarnings("unchecked")
//	
//	protected void contribute(final IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		final String attribute = element.getAttribute("class");
//		final ClassLoader classLoader = context.getClassLoader();
//		final BindedAction basicAction = new DelegatedBindedAction(IAction.AS_PUSH_BUTTON, attribute, classLoader, (AbstractUIElement<Control>) parentContext);
//		basicAction.setEnabled(true);
//		UIElementHandler.handleAction(element, parentContext, basicAction,(AbstractUIElement<Control>) parentContext);
//	}

	
	protected BindedAction contribute(ActionsSetting parentContext, Context context,
			Element element) {
		// TODO Auto-generated method stub
		return null ;
		
	}

//	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		// TODO Auto-generated method stub
//		
//	}

}
