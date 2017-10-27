package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.ui.dialogs.BindedWizard;
import com.onpositive.commons.ui.dialogs.IWizardListener;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.roles.ImageObject;
import com.onpositive.semantic.model.binding.Binding;

public class WizardElementHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final Binding bnd = (Binding) parentContext;
		BindedWizard wizard = new BindedWizard(bnd, element.getAttribute("title"));
		DOMEvaluator.evaluateChilds(element, wizard, context);
		String attribute = element.getAttribute("image");
		if (attribute.length() > 0) {			
			wizard.setDefaultPageImageDescriptor(SWTImageManager.getDescriptor(attribute));			
		}
		String attribute2 = element.getAttribute("listener");
		if (attribute2!=null&&attribute2.length()>0){
			IWizardListener l=(IWizardListener) context.newInstance(attribute2);
			wizard.setWizardListener(l);
		}
		return wizard;
	}
}
