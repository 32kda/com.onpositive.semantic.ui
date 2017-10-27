package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;


public interface IContainerListener {

	void elementAdded(ICompositeElement cnt, IUIElement element);

	void elementRemoved(ICompositeElement cnt, IUIElement element);

}
