package com.onpositive.semantic.generator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class StandartBindingConfigurator implements IBindingConfigurator
{

	public Element getBindingElement(UIElementCandidate candidate, Document doc)
	{
		if (!(candidate instanceof UIPropertyCandidate)) return null;
		Element bindingElement = doc.createElement("binding");
		bindingElement.setAttribute("id", candidate.getId());
		bindingElement.setAttribute("path", candidate.getName());
		if (candidate.getCaption() != null && candidate.getCaption().trim().length() > 0) 
			bindingElement.setAttribute("caption", candidate.getCaption());		
		if (((UIPropertyCandidate)candidate).isRequired()) bindingElement.setAttribute("required", "true");
		if (((UIPropertyCandidate)candidate).isReadOnly()) bindingElement.setAttribute("readonly", "true");
		return bindingElement;
	}

}
