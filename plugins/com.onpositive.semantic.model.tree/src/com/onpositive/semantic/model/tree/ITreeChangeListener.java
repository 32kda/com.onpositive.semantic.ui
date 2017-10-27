package com.onpositive.semantic.model.tree;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public interface ITreeChangeListener<T> {

	void processTreeChange(ITreeNode<T> parentElement,
			ISetDelta<ITreeNode<T>> dlt);

	void processUnknownTreeChange(ITreeNode<T> parentElement);
}
