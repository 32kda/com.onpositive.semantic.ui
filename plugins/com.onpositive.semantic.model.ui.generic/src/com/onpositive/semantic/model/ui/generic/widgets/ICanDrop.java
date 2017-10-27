package com.onpositive.semantic.model.ui.generic.widgets;

public interface ICanDrop {

	boolean canDrop(Object target,Object[] array);

	void drop(Object target, Object[] array);

}
