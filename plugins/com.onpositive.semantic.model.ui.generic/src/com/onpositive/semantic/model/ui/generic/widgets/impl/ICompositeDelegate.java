package com.onpositive.semantic.model.ui.generic.widgets.impl;

public interface ICompositeDelegate extends IElementBehaviorDelegate {

	public void adapt(BasicUIElement<?> element);
	public void unadapt(BasicUIElement<?> element);
	
}
