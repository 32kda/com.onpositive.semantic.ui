package com.onpositive.semantic.model.binding;

import java.io.Serializable;


public interface IBindingSetListener extends Serializable{

	public void bindingChanged(IBindable element, IBinding newBinding,
			IBinding oldBinding);
}
