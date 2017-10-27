package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;

public abstract class AbstractClusterizationPointProvider<T> implements
		IClusterizationPointProvider<T> ,IValueListener{

	HashSet<IValueListener> listeners = new HashSet<IValueListener>();

	public void addChangeListener(
			IValueListener listener) {
		this.listeners.add(listener);
	}

	void fireChange() {
		for (final IValueListener l : this.listeners) {
			l.valueChanged(this, null);
		}
	}

	public Comparator<IClusterizationPoint<T>> getComparator() {
		return null;
	}
	
	public abstract IClusterizationPoint<T>createPoint(Object o);

	public Comparator<T> getRootElementComparator() {
		return null;
	}

	public abstract ISetDelta<IClusterizationPoint<T>> processDelta(
			ISetDelta<T> delta,
			Collection<IClusterizationPoint<T>> currentPoints,
			Collection<T> currentElements);

	public void removeChangeListener(
			IValueListener listener) {
		this.listeners.remove(listener);
	}
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		fireChange();
	}
}