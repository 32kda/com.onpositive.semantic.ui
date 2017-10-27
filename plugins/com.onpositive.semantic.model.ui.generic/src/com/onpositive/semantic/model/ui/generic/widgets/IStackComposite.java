package com.onpositive.semantic.model.ui.generic.widgets;

import java.util.List;

import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public interface IStackComposite {
	public BasicUIElement<?> getTopControl();
	public List<? extends BasicUIElement<?>> getChildren();
	public void setTopControl(BasicUIElement<?> control);
}
