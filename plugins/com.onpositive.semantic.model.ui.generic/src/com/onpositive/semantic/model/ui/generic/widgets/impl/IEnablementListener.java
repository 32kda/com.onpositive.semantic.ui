package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.io.Serializable;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;


public interface IEnablementListener extends Serializable{

	public void enablementChanged(IUIElement<?> element,
			boolean enabled);
}
