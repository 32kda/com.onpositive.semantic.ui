package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IInitializer;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.FilterControl;

public class FilterUIFactory extends UIElementHandler {//тип0 GeneralElementHandler

	public FilterUIFactory() {
	}

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		final FilterControl filterControl = new FilterControl();
		final String attribute = element.getAttribute("markOccurences"); //$NON-NLS-1$
		if (attribute.length() > 0) {
			filterControl.setMarkOccurences(Boolean.parseBoolean(attribute));
		}
		return filterControl;
	}

	public Object handleElement(Element element, Object parentContext, Context context)
	{
		final FilterControl handleElement = (FilterControl) super.handleElement(element, parentContext, context);		
		
		final String attribute = element.getAttribute("targetId"); //$NON-NLS-1$
		if ((attribute != null) && (attribute.length() > 0))
		{
			context.addInitializer( new IInitializer() {

				public void init(Context context) {
					handleElement.setSelector( (AbstractEnumeratedValueSelector<?>)context.getObject(attribute) );
				}
			} );
		}
		return handleElement;
	}

}
