package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.Comparator;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;

public class ListClusterizationPointProvider<T> implements
		IClusterizationPointProvider<T> {

	Collection<? extends IClusterizationPoint<T>> elements;
	boolean inited;

	public ListClusterizationPointProvider(
			Collection<? extends IClusterizationPoint<T>> elements) {
		this.elements = elements;
	}

	public boolean isInited() {
		return this.inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	public Comparator<IClusterizationPoint<T>> getComparator() {
		return null;
	}

	public ISetDelta<IClusterizationPoint<T>> processDelta(ISetDelta<T> delta,
			Collection<IClusterizationPoint<T>> currentPoints,
			Collection<T> currentElements) {
		if (!this.inited) {
			final HashDelta<IClusterizationPoint<T>> result = new HashDelta<IClusterizationPoint<T>>();
			result.getAddedElements().addAll(this.elements);
			this.inited = true;
			return result;
		}
		return RealmNode.noinstance;
	}

	public Comparator<T> getRootElementComparator() {
		return null;
	}
	@Override
	public void addChangeListener(IValueListener listener) {
		
	}
	@Override
	public void removeChangeListener(IValueListener listener) {
		
	}
}
