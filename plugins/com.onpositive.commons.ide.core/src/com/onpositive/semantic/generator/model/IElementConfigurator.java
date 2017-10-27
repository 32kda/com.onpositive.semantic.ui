package com.onpositive.semantic.generator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public interface IElementConfigurator
{
	Element getElement(UIElementCandidate candidate, Document doc);
	public String getName();
	public String getNamespace();
}
