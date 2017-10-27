package com.onpositive.semantic.model.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.onpositive.semantic.model.api.labels.LabelAccess;


public final class LeafNode<T> implements ITreeNode<T>,Comparable<ITreeNode<?>> {

	private final ITreeNode<T> parent;
	private final T element;

	public LeafNode(ITreeNode<T> parent, T element) {
		super();
		this.parent = parent;
		this.element = element;
	}

	public T getElement() {
		return this.element;
	}

	public ITreeNode<T>[] getChildren() {
		return null;
	}

	public ITreeNode<T> getParentNode() {
		return this.parent;
	}

	public boolean hasChildren() {
		return false;
	}

	public String toString() {
		return this.element.toString();
	}

	public void addChangeListener(ITreeChangeListener<?> listener) {

	}

	public void removeChangeListener(ITreeChangeListener<?> listener) {

	}

	public Comparator<ITreeNode<T>> getComparator() {
		return null;
	}

	public int hashCode() {
		return this.element.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof LeafNode) {
			final LeafNode other = (LeafNode) obj;
			if (this.element == null) {
				if (other.element != null) {
					return false;
				}
			} else {
				if (this.element == other.element) {
					return true;
				}
				if (!this.element.equals(other.element)) {
					return false;
				}
			}
			return true;
		}
		if (obj.equals(this.element)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		return (T) com.onpositive.core.runtime.Platform.getAdapter(this.element,
				adapter);
	}

	public boolean contains(Object o) {
		return this.element.equals(o);
	}

	public boolean represents(Object o) {
		return this.contains(o);
	}

	public int size() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<? extends ITreeNode<T>> findPath(List<? extends ITreeNode<T>> current, Object o) {
		if (element.equals(o)){
			if (current==null){
				ArrayList<ITreeNode<T>> arrayList = new ArrayList<ITreeNode<T>>();
				current=arrayList;
				arrayList.add(this);
			}
			else{
				((List)current).add(0,this);
			}
			return current;
		}
		return null;
	}
	

	
	public int compareTo(ITreeNode<?> arg0) {
		Object element2 = arg0.getElement();
		if (element2!=null&&this.element!=null){
			try{
			Comparable d=(Comparable<?>) element2;
			Comparable d1=(Comparable<?>) element;
			return d.compareTo(d1);
			}catch (Exception e) {
				
			}
		}
		String label = LabelAccess.getLabel(element2);
		String label2 = LabelAccess.getLabel(element);
		return label.compareTo(label2);
	}

}
