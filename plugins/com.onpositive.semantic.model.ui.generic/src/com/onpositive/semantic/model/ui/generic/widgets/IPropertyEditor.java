package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.binding.IBindable;

@SuppressWarnings("rawtypes")
public interface IPropertyEditor<T extends IUIElement> extends IBindable {

	
	void setEnablementFromBinding(boolean b);
	
	public String getRole();
	
	@HandlesAttributeDirectly("role")
	public void setRole(String role);

	IUIElement getUIElement();
	
	public Object getUndoContext(); 
}
