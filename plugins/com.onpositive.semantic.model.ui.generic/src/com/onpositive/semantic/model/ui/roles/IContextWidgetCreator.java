package com.onpositive.semantic.model.ui.roles;

import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public interface IContextWidgetCreator extends IWidgetCreator {

	IUIElement<?> createWidget(Binding bnd, WidgetObject object);
}
