package com.onpositive.semantic.model.ui.actions;

import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public interface IBindedActionDelegate {

	public void run(IPropertyEditor<?> binding);
	
	public boolean isEnabled(Object value);
}
