package com.onpositive.commons.xml.language;

import java.util.Collection;

public interface IComposite<T> {

	public void add(T element);
	
	public void remove(Object element);
	
	public Collection<T>elements();
}
