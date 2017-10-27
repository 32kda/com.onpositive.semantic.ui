package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IHasText {

	@HandlesAttributeDirectly("text")
	void setText(String text);
	
	String getText();
}
