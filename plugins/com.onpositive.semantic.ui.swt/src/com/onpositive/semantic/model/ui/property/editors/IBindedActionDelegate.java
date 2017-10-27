package com.onpositive.semantic.model.ui.property.editors;

import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public interface IBindedActionDelegate {

	public void run(IPropertyEditor<?> bindind);
	
	public boolean isEnabled(Object value);
}
