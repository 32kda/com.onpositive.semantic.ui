package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.property.IFunction;

public interface ISingleLineTextElement<T> extends ITextElement<T>{

	
	public IFunction getSelector();

	@HandlesAttributeDirectly("buttonSelector")
	public void setSelector(IFunction selector);
}
