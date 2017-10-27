package com.onpositive.semantic.model.groups;

public interface IRangedValue<T extends Comparable<T>> {

	
	T getMin();
	T getMax();
}
