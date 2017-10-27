package com.onpositive.semantic.model.ui.generic;


import java.io.Serializable;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public interface IElementListener extends Serializable{

	public void elementAdded(ICompositeElement<?, ?> parent,
			IUIElement<?> element);

	public void hierarchyChanged(IUIElement<?> element);

	public void elementRemoved(ICompositeElement<?, ?> parent,
			IUIElement<?> element);

	public void elementCreated(IUIElement<?> element);

	public void elementDisposed(IUIElement<?> element);
	

	public void bindingChanged(IUIElement<?> element,
			IBinding newBinding, IBinding oldBinding);
	
	public void elementVisibilityChanged(IUIElement<?>element);
}
