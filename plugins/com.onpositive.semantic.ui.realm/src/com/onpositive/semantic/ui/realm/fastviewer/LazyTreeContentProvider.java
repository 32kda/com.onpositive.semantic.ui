/**
 * 
 */
package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

final class LazyTreeContentProvider implements ILazyContentProvider {

	private TableViewer owner;
	private FastTreeViewer viewer;
	private ArrayList items = new ArrayList();

	static class State {
		ArrayList items;
		private ITreeNode input;

		public State(ArrayList items, ITreeNode treeNode) {
			super();
			this.items = new ArrayList(items);
			this.input = treeNode;
		}

	}

	public Object getState() {
		return new State(this.items, this.input);
	}

	public void setState(Object object, Object object2) {
		State st = (State) object;
		this.items = st.items;
		this.input = st.input;
		// this.root = new TreeNode();
		updateScreen();
	}

	public LazyTreeContentProvider(FastTreeViewer owner) {
		this.viewer = owner;
	}

	class TreeNode {

		private Object element;

		boolean expanded = true;

		private HashMap children;

		private TreeNode parent;

		public void collapse(Stack row) {
			if (children == null) {
				return;
			}
			RowItem lz = (RowItem) row.pop();
			TreeNode object = (TreeNode) children.get(getSegment(lz));
			if (object != null) {
				if (row.size() > 0) {
					object.collapse(row);
				} else {
					object.expanded = false;
					children.remove(getSegment(lz));
					// if (children.isEmpty()) {
					// children = null;
					// }
				}
			}
		}

		public String toString() {
			StringBuffer rs = new StringBuffer();
			rs.append(element != null ? element.toString() : "");
			rs.append('\r');
			if (children != null) {
				for (Iterator iterator = children.values().iterator(); iterator
						.hasNext();) {
					TreeNode type = (TreeNode) iterator.next();
					rs.append(type.toString());
				}
			}
			return rs.toString();

		}

		public void expand(Stack row) {
			if (children == null) {
				children = new HashMap();
			}
			RowItem lz = (RowItem) row.pop();
			TreeNode object = (TreeNode) children.get(getSegment(lz));

			if (object != null) {
				object.expanded = true;
				if (row.size() > 0) {
					object.expand(row);
				} else {
					TreeNode root2 = object;
					if (root2.children != null) {
						for (Iterator iterator = ((HashMap) root2.children
								.clone()).keySet().iterator(); iterator
								.hasNext();) {
							Object type = (Object) iterator.next();
							TreeNode object2 = (TreeNode) root2.children
									.get(type);
							// if (object2.expanded) {
							LazyTreeContentProvider.this.expand(object2);
							// LazyTreeContentProvider.this
							// .expand((TreeNode) object2);
							// }
						}
					}
				}
			} else {
				TreeNode treeNode = new TreeNode();
				treeNode.parent = this;
				treeNode.element = getSegment(lz);
				children.put(treeNode.element, treeNode);
				if (row.size() > 0) {
					treeNode.expand(row);
				}
			}
		}
	}

	TreeNode root = new TreeNode();
	// private HashSet expanded = new HashSet();
	private ITreeNode input;

	private Comparator comparator;
	private Thread resolver;

	public void updateElement(int index) {
		owner.replace(items.get(index), index);
	}

	public void dispose() {
		if (resolver != null) {
			resolver.interrupt();
		}
		for (Iterator threads = new ArrayList(this.threads).iterator(); threads
				.hasNext();) {
			Thread object = (Thread) threads.next();
			object.stop();
		}
	}

	static class TempNode implements ITreeNode {

		public List<?> getChildren() {
			return null;
		}

		public boolean hasChildren() {
			return false;
		}

		public String toString() {
			return "Calculating content";
		}

		
		public Object getNodeObject() {
			return null;
		}

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null) {
			return;
		}
		// root = new TreeNode();
		// expanded.clear();
		this.owner = (TableViewer) viewer;
		final ITreeNode node = (ITreeNode) newInput;
		this.input = node;
		// captureSelection();
		setNodes(new ArrayList(Collections.singletonList(new TempNode())));
		resolver = new Thread() {
			public void run() {
				List children = node.getChildren();
				asyncSet(children);
				resolver = null;
			}
		};
		resolver.start();
	}

	void captureSelection() {
		if (owner != null && !inChange) {
			TableItem[] selection = owner.getTable().getSelection();
			if (selection.length == 1) {
				si = (RowItem) selection[0].getData();
			} else {
				si = null;
			}
		}

	}

	public void asyncSet(final List children) {

		Table table = owner.getTable();
		if (!table.isDisposed()) {
			table.getDisplay().syncExec(new Runnable() {

				public void run() {
					if (owner.getTable().isDisposed()) {
						return;
					}
					setNodes(children);
					viewer.notifyContentInited();
					if (!LazyTreeContentProvider.this.expandFirst) {
						expand(root);
					} else {
						root = new TreeNode();
					}
					updateScreen();
					if (LazyTreeContentProvider.this.expandFirst) {
						viewer.triggerExpandCollapse((RowItem) items.get(0));
					}

				}
			});
		}
	}

	public void update() {

		ITreeNode node = (ITreeNode) this.input;
		if (node == null) {
			return;
		}
		List children = node.getChildren();
		setNodes(children);
		TreeNode root2 = root;
		// root = new TreeNode();
		// /.println(root2);
		expand(root2);
		updateScreen();
	}

	private void expand(final TreeNode root2) {
		if (!isTree) {
			return;
		}
		if (root2.children != null && root2.expanded) {
			for (Iterator iterator = new ArrayList(root2.children.keySet())
					.iterator(); iterator.hasNext();) {
				final Object type = (Object) iterator.next();
				final Object type2 = root2.children.get(type);
				expandNode(root2, type2, new Runnable() {

					public void run() {
						expand((TreeNode) type2);
					}

				});

			}
		}
	}

	private void expandNode(TreeNode type, Object type2, Runnable runnable) {
		if (!isTree) {
			return;
		}
		int size = items.size();
		for (int b = 0; b < size; b++) {
			RowItem row = (RowItem) items.get(b);
			if (isSameKey((TreeNode) type2, row)) {
				if (!row.isExpanded()) {
					viewer.expandNode(row, runnable);
					return;
				} else {
					return;
				}
			}
		}
	}

	public Object getSegment(RowItem row) {
		return row.data.getNodeObject();
	}

	private boolean isSameKey(TreeNode type, RowItem row) {
		if (type == null || row == null) {
			return false;
		}
		TreeNode c = (TreeNode) type;
		Object segment = getSegment(row);
		if (segment == c.element || segment instanceof String
				&& segment.equals(c.element)) {
			RowItem parentItem = row.getParentItem();
			TreeNode parent = c.parent;
			if (parentItem == null
					&& (parent == null || parent.element == null)) {
				return true;
			}
			return isSameKey(parent, parentItem);
		}
		return false;
	}

	public boolean isEq(ITreeNode tnode, ITreeNode data) {
		if (tnode == null || data == null) {
			return false;
		}
		if (tnode.getClass() != data.getClass()) {
			return false;
		}
		if (data == null) {
			if (tnode == null) {
				return true;
			}
			return false;
		}
		if (tnode == null) {
			if (data == null) {
				return true;
			}
			return false;
		}
		return data.equals(tnode);
	}

	private Vector threads = new Vector();
	private boolean expandFirst;

	public void addThread(Thread s) {
		threads.add(s);
	}

	public void removeThread(Thread s) {
		threads.remove(s);
	}

	private ISelector selector;
	private RowItem si;
	public boolean inChange;
	private boolean isTree;

	public ISelector getSelector() {
		return selector;
	}

	public void setSelector(ISelector selector) {
		this.selector = selector;
	}

	private void setNodes(List children) {

		if (children.isEmpty()) {
			children = new ArrayList();
			children.add(new ITreeNode() {

				public List getChildren() {
					return null;
				}

				public boolean hasChildren() {
					return false;
				}

				public String toString() {
					return "All items was filtered from view";
				}

				
				public Object getNodeObject() {
					return null;
				}

			});
		}
		clearThreads();
		if (comparator != null) {
			Collections.sort(children, comparator);
		}
		items.clear();
		for (int a = 0; a < children.size(); a++) {
			ITreeNode node = (ITreeNode) children.get(a);
			items.add(new RowItem(a, null, node, selector));
		}
		owner.setItemCount(children.size());

		updateScreen();

		owner.getControl().redraw();
		owner.getControl().update();

		if (si != null) {
			// /.println(si);
			for (int a = 0; a < items.size(); a++) {
				final int b = a;
				RowItem object = (RowItem) items.get(a);
				if (isEq(object.data, si.data)) {

					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							owner.getTable().setTopIndex(Math.max(b - 2, 0));
							owner.getTable().select(b);
							updateScreen();
							owner.getTable().redraw();
							owner.getTable().update();
						}

					});

					// int i = owner.getTable().getItemHeight();
					return;
					// break;
				}
			}
		}

	}

	private synchronized void clearThreads() {
		for (Iterator threads = new ArrayList(this.threads).iterator(); threads
				.hasNext();) {
			Thread object = (Thread) threads.next();
			object.interrupt();
		}
		threads = new Vector();
	}

	public RowItem getRow(int rowNumber) {
		return (RowItem) items.get(rowNumber);
	}

	public void insertChildren(RowItem row, List children) {
		internalExpand(row, children);
		updateScreen();
	}

	Stack getPath(RowItem row) {
		Stack c = new Stack();
		while (row != null) {
			c.add(row);
			row = row.getParentItem();
		}
		return c;
	}

	private void internalExpand(RowItem row, List children) {
		if (children == null) {
			return;
		}

		if (comparator != null) {
			Collections.sort(children, comparator);
		}
		int position = row.index;

		for (int a = position + 1; a < items.size(); a++) {
			RowItem rowItem = (RowItem) items.get(a);
			rowItem.index += children.size();
			rowItem.lastChildIndex += children.size();
		}
		items.addAll(position + 1, convertToItems(row, children));
		RowItem parentItem = row;
		while (parentItem != null) {
			parentItem.lastChildIndex += children.size();
			parentItem = parentItem.getParentItem();
		}
		root.expand(getPath(row));

	}

	private void updateScreen() {
		owner.setItemCount(items.size());
		int topIndex = Math.max(0, owner.getTable().getTopIndex() - 10);
		int i = Math.min(topIndex + owner.getTable().getSize().y
				/ owner.getTable().getItemHeight() + 40, items.size());
		for (int a = topIndex; a < i; a++) {
			owner.replace(items.get(a), a);
		}
	}

	public void collapseChildren(RowItem row) {
		root.collapse(getPath(row));
		collapseChildren2(row);
		updateScreen();
	}

	private Collection convertToItems(RowItem row, List children) {
		ArrayList its = new ArrayList();
		int a = 1;
		if (children != null) {
			for (int b = 0; b < children.size(); b++) {
				ITreeNode node = (ITreeNode) children.get(b);
				int index = row.index + a;
				RowItem rowItem = new RowItem(index, row, node, selector);
				its.add(rowItem);
				a++;
			}
		}
		return its;
	}

	public RowItem[] getVisibleItems() {
		if (owner == null) {
			return new RowItem[0];
		}
		int topIndex = owner.getTable().getTopIndex();
		int i = Math.min(topIndex + owner.getTable().getSize().y
				/ owner.getTable().getItemHeight() + 5, items.size());
		if (i - topIndex < 0) {
			return new RowItem[0];
		}
		RowItem[] result = new RowItem[i - topIndex];
		for (int a = topIndex; a < i; a++) {
			result[a - topIndex] = (RowItem) items.get(a);
		}
		return result;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
		if (!inChange) {
			update();
		}
	}

	public DrillFrame goForward(ITreeNode sl) {
		setNodes(sl.getChildren());
		DrillFrame dr = new DrillFrame(input, null, null, root);
		input = sl;
		this.root = new TreeNode();
		return dr;
	}

	public void restore(DrillFrame frame) {
		Object element = frame.getElement();
		root = (TreeNode) frame.getRoot();
		setNodes(((ITreeNode) element).getChildren());
		expand(root);
		input = (ITreeNode) element;
	}

	public List getChildItems(RowItem row) {
		if (row.isExpanded()) {
			ArrayList ls = new ArrayList();
			for (int a = row.index + 1; a <= row.lastChildIndex
					&& a < items.size(); a++) {
				RowItem object = (RowItem) items.get(a);
				ls.add(object);
				if (object.isExpanded()) {
					a = object.lastChildIndex;
				}
			}
			return ls;
		}
		return Collections.emptyList();
	}

	public int getItemCount() {
		return items.size();
	}

	public void collapseAll() {
		this.root = new TreeNode();
		this.update();
	}

	public void cleanupExpanded() {
		// this.root = new TreeNode();
	}

	public void setExpandFirst(boolean b) {
		this.expandFirst = b;
	}

	public synchronized boolean hasThead(Thread runnable) {
		return threads.contains(runnable);
	}

	public Object getInput() {
		return input;
	}

	public void collapseChildren2(RowItem row) {
		ArrayList newItems = new ArrayList(items.size());
		for (int a = 0; a <= row.index; a++) {
			newItems.add(items.get(a));
		}
		int size = row.lastChildIndex - row.index;
		for (int a = row.lastChildIndex + 1; a < items.size(); a++) {
			RowItem e = (RowItem) items.get(a);
			e.index -= size;
			e.lastChildIndex -= size;
			newItems.add(e);
		}
		items = newItems;
		RowItem parentItem = row;
		while (parentItem != null) {
			parentItem.lastChildIndex -= size;
			parentItem = parentItem.getParentItem();
		}

	}

	public void cleanupExpanded(boolean isTree) {
		this.isTree = isTree;
	}
}