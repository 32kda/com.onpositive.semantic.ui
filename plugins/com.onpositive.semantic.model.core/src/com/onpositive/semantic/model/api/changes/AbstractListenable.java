package com.onpositive.semantic.model.api.changes;

import java.util.LinkedHashSet;

public class AbstractListenable implements IListenable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	protected LinkedHashSet<IValueListener> listeners = new LinkedHashSet<IValueListener>();

	public AbstractListenable() {
		super();
	}
	public boolean hasListeners(){
		return !listeners.isEmpty();
	}

	public void addValueListener(IValueListener<?> listener) {
		this.listeners.add(listener);
	}

	public void removeValueListener(IValueListener<?> listener) {
		this.listeners.remove(listener);
	}
	protected void fireChanged(){
		fireChanged(null, this);
	}
	protected void fireChanged(Object data) {
		fireChanged(null, data);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void fireChanged(Object oldData,Object data) {
		for (final IValueListener l : this.listeners.toArray(new IValueListener[listeners.size()])) {
			l.valueChanged(oldData, data);
		}
	}

}