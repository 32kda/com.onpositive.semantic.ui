package com.onpositive.businessdroids.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractListenable {

	PropertyChangeSupport support;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (support==null){
			support=new PropertyChangeSupport(this);
		}
		support.addPropertyChangeListener(listener);
	}

	public void firePropertyChange(PropertyChangeEvent event) {
		if (support!=null){
		support.firePropertyChange(event);
		}
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		if (support!=null){
		support.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		if (support!=null){
		support.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (support!=null){
			support.removePropertyChangeListener(listener);
			if (support.getPropertyChangeListeners().length==0){
				support=null;
			}
		}
		
	}
	
	
}
