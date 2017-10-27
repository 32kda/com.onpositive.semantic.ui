package com.onpositive.semantic.model.tree;

import java.util.Collection;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public interface IClusterNodeCallback<T> {

	void add(T element);

	void remove(T element);

	void setVisible(boolean value);

	boolean isVisible();

	IClusterizationPoint<T> getOwner();

	Collection<T> getParentClusterElements();

	Collection<T> getPointElements();

	ISetDelta<T> getDelta();

	void removeChanged(T element);
}
