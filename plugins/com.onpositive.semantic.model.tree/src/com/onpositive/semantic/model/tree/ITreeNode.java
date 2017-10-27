package com.onpositive.semantic.model.tree;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import com.onpositive.commons.platform.registry.IAdaptable2;
import com.onpositive.semantic.model.api.labels.IHasPresentationObject;


public interface ITreeNode<T> extends IAdaptable2,IHasPresentationObject,Serializable{

	boolean hasChildren();

	ITreeNode<T>[] getChildren();

	ITreeNode<T> getParentNode();

	Object getElement();

	void addChangeListener(ITreeChangeListener<?> listener);

	void removeChangeListener(ITreeChangeListener<?> listener);

	Comparator<ITreeNode<T>> getComparator();

	boolean contains(Object o);

	boolean represents(Object o);

	int size();

	List<? extends ITreeNode<T>> findPath(List<? extends ITreeNode<T>>current, Object o);
	
}
