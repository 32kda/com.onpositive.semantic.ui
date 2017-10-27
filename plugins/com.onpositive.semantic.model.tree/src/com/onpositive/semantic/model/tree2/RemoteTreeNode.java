
package com.onpositive.semantic.model.tree2;

import java.util.List;

import com.onpositive.semantic.model.tree.ITreeNode;

@SuppressWarnings({ "unchecked", "serial" })
public abstract class RemoteTreeNode extends AbstractTreeNode {

	int size=-1;
	
	public RemoteTreeNode(Object element, ITreeNode<?> parent) {
		super(element, parent);
	}

	
	public int size() {		
		return size;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List findPath(List current, Object o) {
		return null;
	}
	
}
