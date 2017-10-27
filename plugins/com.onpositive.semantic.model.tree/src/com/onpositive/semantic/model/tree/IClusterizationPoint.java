package com.onpositive.semantic.model.tree;

import com.onpositive.semantic.model.api.changes.ISetDelta;


public interface IClusterizationPoint<T> extends
		Comparable<IClusterizationPoint<T>> {

	/**
	 * @param kind
	 * @param elements
	 * @return
	 */
	void processDelta(ISetDelta<T> changes, IClusterNodeCallback<T> callback);

	IClusterizationPointProvider<T> getSubClusterizationProvider();

	Object getPrimaryValue();

	<T> T getAdapter(Class<T> adapter);
}
