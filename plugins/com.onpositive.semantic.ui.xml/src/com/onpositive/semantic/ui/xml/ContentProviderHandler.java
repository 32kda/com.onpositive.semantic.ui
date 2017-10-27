package com.onpositive.semantic.ui.xml;

import org.eclipse.jface.viewers.IContentProvider;
import org.w3c.dom.Element;

import com.onpositive.commons.Activator;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class ContentProviderHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		try{
		ListEnumeratedValueSelector<?>selector=(ListEnumeratedValueSelector<?>) parentContext;		
		Object newInstance = context.newInstance(element,"class");
		selector.setContentProvider((IContentProvider) newInstance);
		}catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}

}
