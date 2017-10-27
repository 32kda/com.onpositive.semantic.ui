package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Observer;

public class ChangableValue implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Object value;
	
	HashSet<Observer>observers=new HashSet<Observer>();
	
	public void addObserver(Observer observer){
		this.observers.add(observer);
	}
	
	public void removeObserver(Observer observer){
		this.observers.remove(observer);
	}
	
	public void setValue(Object value){
		if (this.value==value){
			return;
		}
		this.value=value;
		for (Observer s:observers){
			s.update(null, value);
		}
	}
	
	public Object getValue(){
		return this.value;
	}
}
