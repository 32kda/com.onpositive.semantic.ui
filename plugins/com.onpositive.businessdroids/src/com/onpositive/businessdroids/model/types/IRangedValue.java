package com.onpositive.businessdroids.model.types;

public interface IRangedValue<T extends Comparable<T>> {

	
	T getMin();
	T getMax();
}
