package com.onpositive.semantic.model.api.changes;

import java.io.Serializable;
import java.util.Collection;

public interface ISetDelta<T> extends Serializable{
	
	Object getFirstAddedElement();
	
	Object getFirstRemovedElement();
	
	Object getFirstChangedElement();

	Collection<T> getAddedElements();

	Collection<T> getRemovedElements();

	Collection<T> getChangedElements();
	
	public boolean isOrderChanged() ;

	boolean isEmpty();

	ISetDelta<Object> getSubDelta(T element);
}
