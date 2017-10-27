package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Activator;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.editactions.RunnableAction;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

@SuppressWarnings("restriction")
public class CopyActionHandler extends AbstractActionElementHandler {

	private static final String COPY_IMAGE_ID = "com.onpositive.semantic.ui.copy";

	@SuppressWarnings({ "unchecked" })
	@Override
	protected IContributionItem contribute(ActionsSetting parentContext,
			Context context, Element element) {
		final IListElement<?> control = (IListElement<Object>) parentContext.getControl();
		IContributionItem copyAction = null;
		final String className = element.getAttribute("customHandler");
		if (className != null && !className.isEmpty()) {
			try {
				Class<?> customHandlerClass = context.getClassLoader().loadClass(className);
				if (Runnable.class.isAssignableFrom(customHandlerClass)) {
					Runnable runnable = (Runnable) customHandlerClass.newInstance();
					copyAction = new RunnableAction(runnable);
				}
			} catch (ClassNotFoundException e) {
				Activator.log(e);
			} catch (InstantiationException e) {
				Activator.log(e);
			} catch (IllegalAccessException e) {
				Activator.log(e);
			}
		} 
		if (copyAction == null)
			copyAction = control.createCopyContributionItem();
		handleAction(element,parentContext,(Action) copyAction,parentContext.getControl());
		if (((Action) copyAction).getImageDescriptor() == null && ((Action) copyAction).getImageId() == null)
			((Action) copyAction).setImageId(COPY_IMAGE_ID);
		return copyAction;
	}

}
