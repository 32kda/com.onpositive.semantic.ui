package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;

public class DataSourceHandler extends GeneralElementHandler {

	public DataSourceHandler() {
		super(null, null);
		setObjectClass(Binding.class);// this is for validation
	}

	protected Object produceNewInstance(Element element, Object parentContext,
			Context context) {
		String attrDataUrl = element.getAttribute("url");
		String attribute = element.getAttribute("class");
		if (attribute != null) {
			attrDataUrl += '/' + attribute;
		}
		Object resolve = GlobalAccess.resolve(attrDataUrl, Object.class);
		if (resolve instanceof IBinding) {
			IBinding l = (IBinding) resolve; //$NON-NLS-1$ //$NON-NLS-2$
			return l;
		} else {
			Binding binding = new Binding(resolve){
				protected boolean isReadonlyWithoutProperty() {
					return false;
				}
			};
			binding.setReadOnly(false);
			return binding;
		}
	}
}
