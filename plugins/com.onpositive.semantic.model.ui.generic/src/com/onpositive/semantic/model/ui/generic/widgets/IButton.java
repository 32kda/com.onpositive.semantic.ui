package com.onpositive.semantic.model.ui.generic.widgets;

public interface IButton<T> extends IUIElement<T>,ICanBeReadOnly<T>{

	boolean getSelection();
	void setSelection(boolean selected);
	
}
