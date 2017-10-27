package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;

public class ListeningVariable extends VariableExpression implements
		IValueListener<Object> {
	
	private static final long serialVersionUID = 1L;


	public ListeningVariable(){
		
	}
	public ListeningVariable(Object value){
		setValue(value);
	}

	
	@Override
	protected void setNewValue(Object newValue) {
		ObjectChangeManager.removeWeakListener(value, this);		
		ObjectChangeManager.addWeakListener(newValue, this);		
		super.setNewValue(newValue);
	}

	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		fireChanged();
	}
}
