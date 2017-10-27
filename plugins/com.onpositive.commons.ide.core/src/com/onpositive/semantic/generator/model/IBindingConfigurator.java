package com.onpositive.semantic.generator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public interface IBindingConfigurator
{
	Element getBindingElement(UIElementCandidate candidate, Document doc);
}
