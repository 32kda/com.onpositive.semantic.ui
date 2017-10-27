package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.changes.AbstractListenable;


public class AbstractListenableExpression<T> extends AbstractListenable implements
		IListenableExpression<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Object value;

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getValue() {
		return (T) this.value;
	}


	protected void setNewValue(Object newValue) {
		final Object l = this.value;
		if ((this.value == null) && (newValue == null)) {
			return;
		}	
		this.value = newValue;
		//ObjectChangeManager.markChanged(this);
		fireChanged(l, newValue);
	}

	public void dispose() {

	}
	
	@Override
	public void disposeExpression() {
		this.dispose();
	}

}
