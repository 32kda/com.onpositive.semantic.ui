package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface ICanBeReadOnly<T> extends IUIElement<T>{

	@HandlesAttributeDirectly("readonly")
	public void setReadOnly(boolean parseBoolean);

	public boolean isReadOnly() ;	
}
