package com.onpositive.semantic.model.ui.generic.widgets;

public interface IClickableElement<T> extends ICanBeReadOnly<T> {

	void setImage(String image);

	String getImage();

	boolean needsLabel();

}
