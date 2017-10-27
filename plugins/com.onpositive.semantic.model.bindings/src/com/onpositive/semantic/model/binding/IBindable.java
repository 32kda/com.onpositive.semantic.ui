package com.onpositive.semantic.model.binding;

import com.onpositive.commons.xml.language.HandlesParent;


public interface IBindable {

	@HandlesParent
	void setBinding(IBinding binding);

	IBinding getBinding();
	
	void addBindingSetListener(IBindingSetListener listener);

	void removeBindingSetListener(IBindingSetListener listener);
}
