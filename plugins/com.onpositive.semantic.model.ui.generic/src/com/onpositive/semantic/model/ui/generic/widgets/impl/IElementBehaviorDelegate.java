package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.io.Serializable;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public interface IElementBehaviorDelegate extends Serializable{
	
	void onCreateStart(BasicUIElement<?> element);
	void onCreateEnd(BasicUIElement<?> element);
	void onDispose(BasicUIElement<?> element);
	void processValueChange(ISetDelta<?> valueElements);
	void internalSetBinding(IBinding binding2);
	void setValue(Object value);
	public void handleChange(IUIElement<?> b,Object value);		
}
