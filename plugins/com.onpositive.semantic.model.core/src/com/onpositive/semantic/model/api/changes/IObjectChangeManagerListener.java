package com.onpositive.semantic.model.api.changes;

import java.io.Serializable;

public interface IObjectChangeManagerListener extends Serializable{

	void realmRegisted(INotifyableCollection<?>collection);
	void realmUnRegisted(INotifyableCollection<?>collection);
	
	void listenerAdded(Object object,IValueListener<?>listener);
	
	void listenerRemoved(Object object,IValueListener<?>listener);
}
