package com.onpositive.businessdroids.model;

/**
 * Abstract interface 
 * @author kor
 *
 */
public interface IArray extends Iterable<Object>{

	public abstract int getItemCount();

	public abstract Object getItem(int i);
	
	Class<?>getComponentType();
}
