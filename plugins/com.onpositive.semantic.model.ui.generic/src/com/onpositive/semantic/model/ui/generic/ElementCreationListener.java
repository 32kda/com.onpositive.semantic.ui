package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public abstract class ElementCreationListener implements
		IElementListener {

	public void hierarchyChanged(IUIElement<?> element) {
		
	}

	public void bindingChanged(IUIElement<?> element,
			IBinding newBinding, IBinding oldBinding) {

	}

	public void elementAdded(ICompositeElement<?,?> parent,
			IUIElement<?> element) {

	}

	public void elementRemoved(ICompositeElement<?,?> parent,
			IUIElement<?> element) {

	}

	public void elementDisposed(IUIElement<?> element) {

	}

	@Override
	public void elementVisibilityChanged(IUIElement<?> element) {
		
	}
}
