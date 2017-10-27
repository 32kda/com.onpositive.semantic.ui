package com.onpositive.semantic.generator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class StandartElementConfigurator implements IElementConfigurator
{

	public Element getElement(UIElementCandidate candidate, Document doc)
	{
		Element stringElement = doc.createElement(getName());
		stringElement.setAttribute("bindTo", candidate.getId());
		return stringElement;
	}


	

}
