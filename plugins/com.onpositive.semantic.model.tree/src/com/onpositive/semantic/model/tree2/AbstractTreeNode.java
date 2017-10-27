package com.onpositive.semantic.model.tree2;

import java.util.Comparator;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.labels.IHasPresentationObject;
import com.onpositive.semantic.model.tree.ITreeChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;

public abstract class AbstractTreeNode implements ITreeNode<Object>,IHasPresentationObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Object element;
	protected final ITreeNode<?> parent;

	

	public AbstractTreeNode(Object element, ITreeNode<?> parent) {
		super();
		this.element = element;
		this.parent = parent;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ITreeNode getParentNode() {
		return parent;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.isInstance(this)){
			return adapter.cast(this);
		}
		return null;
	}
	
	@Override
	public Object getElement() {
		return element;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}
	
	
	protected LinkedHashSet<ITreeChangeListener<?>>l;

	@SuppressWarnings("rawtypes")
	public void addChangeListener(ITreeChangeListener listener) {
		if (l==null){
			l=new LinkedHashSet<ITreeChangeListener<?>>();
		}
		l.add(listener);
	}

	
	@SuppressWarnings("rawtypes")
	public void removeChangeListener(ITreeChangeListener listener) {
		if (l==null){
			return;
		}
		l.remove(listener);
		if (l.isEmpty()){
			l=null;
		}
	}

	@Override
	public Comparator<ITreeNode<Object>> getComparator() {
		return null;
	}

	@Override
	public boolean represents(Object o) {
		Object element = getElement();
		if (element==null){
			return o==null;
		}
		return element.equals(o);
	}


}