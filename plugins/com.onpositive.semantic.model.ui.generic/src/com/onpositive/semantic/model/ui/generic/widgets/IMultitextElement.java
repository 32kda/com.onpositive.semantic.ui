package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IMultitextElement<T> extends ITextElement<T>{

	public boolean isWrapText();
	
	@HandlesAttributeDirectly("wrapText")
	public void setWrapText(boolean wrapText);

	public boolean isMultiline();

	@HandlesAttributeDirectly("multiline")
	public void setMultiline(boolean isMultiline) ;
}
