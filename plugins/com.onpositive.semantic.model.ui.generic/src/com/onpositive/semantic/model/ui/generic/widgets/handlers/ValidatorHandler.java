package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.binding.Binding;

public class ValidatorHandler extends GeneralElementHandler {

	public ValidatorHandler() {
		super( null, null ) ;
	}

	
	protected Object  produceNewInstance(Element element, Object parentContext, Context context)
	{
		final Element el = element;
		Binding parentBinding=(Binding) parentContext;
		final String attrValue = el.getAttribute("value"); //$NON-NLS-1$
		try {
			final Class<?> loadClass = context.getClassLoader().loadClass( attrValue ) ;

			try {
				final IValidator<?> newInstance = (IValidator<?>) loadClass.newInstance();
				parentBinding.addValidator(newInstance);
			} catch (final InstantiationException e) {
				Platform.log(e);
			} catch (final IllegalAccessException e) {
				Platform.log(e);
			}
		} catch (final ClassNotFoundException e) {
			Platform.log(e);
		}
		return null;
	}
}