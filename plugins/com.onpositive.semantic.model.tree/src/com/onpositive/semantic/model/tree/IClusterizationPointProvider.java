package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.Comparator;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;

public interface IClusterizationPointProvider<T> {

	ISetDelta<IClusterizationPoint<T>> processDelta(ISetDelta<T> delta,
			Collection<IClusterizationPoint<T>> currentPoints,
			Collection<T> currentElements);

	Comparator<IClusterizationPoint<T>> getComparator();

	Comparator<T> getRootElementComparator();

	void addChangeListener(
			IValueListener listener);

	void removeChangeListener(
			IValueListener listener);
}
