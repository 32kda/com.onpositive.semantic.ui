package com.onpositive.semantic.model.api.changes;


public interface INotifyableCollection<T> {

	void changed(ISetDelta<?> dlt);
	
	boolean contains(Object obj);
}
