package com.onpositive.semantic.ui.realm.fastviewer;

import java.lang.ref.WeakReference;
import java.util.List;

public class RowItem {

	int index;

	int lastChildIndex;

	RowItem parentItem;

	ITreeNode data;

	private WeakReference children;

	private ISelector selector;

	public RowItem(int index, RowItem parentItem, ITreeNode data,
			ISelector selector) {
		super();
		this.index = index;
		this.lastChildIndex = index;
		this.parentItem = parentItem;
		this.data = data;
		this.selector = selector;
	}

	public RowItem(RowItem row) {
		this.index = row.index;
		this.selector = row.selector;
		this.lastChildIndex = row.lastChildIndex;
		this.parentItem = row.parentItem == null ? row.parentItem
				: new RowItem(row.parentItem);
		this.data = row.data;
		this.children = row.children;
	}

	public boolean isExpanded() {
		return lastChildIndex > index;
	}

	public void expand() {

	}

	public RowItem getParentItem() {
		return parentItem;
	}

	public String toString() {
		return (parentItem != null ? parentItem.toString() + ":" : "")
				+ data.toString();
	}

	public boolean isExpandable() {
		return hasChildren();
	}

	public ITreeNode getNode() {
		return data;
	}

	public int getLevel() {
		int level = 0;
		RowItem p = this.parentItem;
		while (p != null) {
			level++;
			p = p.parentItem;
		}
		return level;
	}

	public List getChildren() {
		// if (children != null) {
		// List l = (List) children.get();
		// if (l != null) {
		// return l;
		// }
		// }
		List children3 = data.getChildren();
		children3 = convert(children3);
		List children2 = children3;
		children = new WeakReference(children2);
		return children2;
	}

	private List convert(List children3) {		
		return children3;
	}

	private boolean hasChildrenCalced = false;

	private boolean hasChildren;

	public boolean hasChildren() {
		if (hasChildrenCalced) {
			return hasChildren;
		}
		hasChildren = data.hasChildren();
		if (!hasChildren) {
			return false;
		}
		return true;
	}
}
