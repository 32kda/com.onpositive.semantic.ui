package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.Activator;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;

public class RealmLazyTreeContentProvider implements ILazyTreeContentProvider,
		IRealmContentProvider, ISimpleChangeListener<IFilter>,
		ITreeChangeListener<Object> {

	private TreeViewer viewer;

	private HashSet<IFilter> filters;
	private final HashSet<ISimpleChangeListener<IRealmContentProvider>> listeners = new HashSet<ISimpleChangeListener<IRealmContentProvider>>();

	private boolean inverse;

	private Comparator<Object> comparator;

	private ITreeNode<?> node;

	private ListEnumeratedValueSelector<?> selector;

	public RealmLazyTreeContentProvider(ListEnumeratedValueSelector<?> selector) {
		this.selector = selector;
	}

	@SuppressWarnings("unchecked")
	public Object mapValue(Object value) {
		if (value instanceof Object[]) {
			return this.mapValue(Arrays.asList((Object[]) value));
		}
		if (value instanceof Collection) {
			return this.mapValue((Collection) value);
		}
		return this.mapValue(Collections.singleton(value));
	}

	private ITreeNode<?> findTreeNode(ITreeNode<?> node, Object o) {
		if (node.contains(o)) {
			if (node.represents(o)) {
				return node;
			}
			for (final ITreeNode<?> n : node.getChildren()) {
				final ITreeNode<?> findTreeNode = this.findTreeNode(n, o);
				if (findTreeNode != null) {
					return findTreeNode;
				}
			}
		} else {
			return null;
		}
		return null;
	}

	public Object mapValue(Collection<Object> value) {
		final ArrayList<ITreeNode<?>> result = new ArrayList<ITreeNode<?>>();
		if (this.node != null) {
			for (final Object o : value) {
				final ITreeNode<?> findTreeNode = this.findTreeNode(this.node,
						o);
				if (findTreeNode != null) {
					result.add(findTreeNode);
				}
			}
		}
		return result;
	}

	public Object getParent(Object element) {
		if (element instanceof ITreeNode<?>) {
			final ITreeNode<?> node = (ITreeNode<?>) element;
			return node.getParentNode();
		}
		return null;
	}

	public void updateChildCount(final Object element, int currentChildCount) {
		boolean k = needEndRefresh;
		needEndRefresh = false;
		final ITreeNode<?> node = (ITreeNode<?>) element;
		final ITreeNode<?>[] children = this.getChildren(node);
		if (children != null) {
			final int min = Math.min(children.length,
					currentChildCount + 100000);
			this.viewer.setChildCount(element, min);
			if (min != children.length) {
				this.viewer.getControl().getDisplay().asyncExec(new Runnable() {

					public void run() {
						if (!RealmLazyTreeContentProvider.this.viewer
								.getControl().isDisposed()) {
							RealmLazyTreeContentProvider.this.updateChildCount(
									element, min);
						}
					}

				});
			}
		}
		if (k) {
			endRefresh();
		}
	}

	public void updateElement(Object parent, int index) {
		try {
			final ITreeNode<?> pnode = (ITreeNode<?>) parent;
			final ITreeNode<?>[] children = this.getChildren(pnode);
			final int i = children.length - index - 1;
			if (i < 0) {
				return;
			}
			final ITreeNode<?> treeNode = children[this.inverse ? index : i];
			//viewer.setChildCount(parent,parent==viewer.getInput()? children.length+1:children.length);
			
			this.viewer.replace(parent, index, treeNode);
			
			if (treeNode.hasChildren()) {
				ITreeNode<?>[] children2 = getChildren(treeNode);
				if (children2!=null&&children2.length>0){
				if (this.viewer.getExpandedState(treeNode)) {
					this.viewer.setHasChildren(treeNode, true);
				} else {
					this.viewer.setHasChildren(treeNode, true);
				}
				}
				else{
					this.viewer.setHasChildren(treeNode, false);	
				}
			} else {
				this.viewer.setHasChildren(treeNode, false);
			}
			
		} catch (Exception e) {
			Activator.log(e);
		}
	}

	private final HashMap<ITreeNode<?>, ITreeNode<?>[]> sortedMap = new HashMap<ITreeNode<?>, ITreeNode<?>[]>();

	private StructuredSelection selection;

	@SuppressWarnings("unchecked")
	ITreeNode<?>[] getChildren(ITreeNode<?> pnode) {
		if (pnode == null) {
			return new ITreeNode[0];
		}
		final ITreeNode<?>[] objects = this.sortedMap.get(pnode);
		if (objects != null) {
			return objects;
		}
		ITreeNode<?>[] children = pnode.getChildren();
		if (children != null) {
			final ArrayList<ITreeNode<?>> mn = new ArrayList<ITreeNode<?>>();

			l2: for (final ITreeNode<?> c : children) {
				if (this.filters != null) {
					for (final IFilter f : this.filters) {
						if (!f.accept(c)) {
							continue l2;
						}
					}
				}
				mn.add(c);
			}
			children = mn.toArray(new ITreeNode[mn.size()]);
			if (this.comparator != null) {
				Arrays.sort((Object[]) children, this.comparator);
			} else {
				Arrays.sort((Object[]) children, (Comparator) pnode
						.getComparator());
			}
			this.sortedMap.put(pnode, children);
		}
		return children;
	}

	public void dispose() {
		ep = null;
		if (this.node != null) {
			this.node.removeChangeListener(this);
		}

	}

	boolean needEndRefresh;

	private Object[] ep;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
//		if (newInput != null) {
//			startRefresh();
//		}
		needEndRefresh = true;

		this.sortedMap.clear();
		if (this.node != null) {
			this.node.removeChangeListener(this);
		}
		final ITreeNode<?> newINode = (ITreeNode<?>) newInput;
		if (newINode != null) {
			newINode.addChangeListener(this);
		}
		this.node = newINode;

	}

	public void setComparator(Comparator<? extends Object> cmp, boolean inverse) {
		this.inverse = inverse;
		if (this.comparator != cmp) {
			this.comparator = (Comparator<Object>) cmp;
			this.sortedMap.clear();
			if (viewer != null) {
				viewerRefresh();
			}
		} else {
			viewerRefresh();
		}
		Display.getCurrent().asyncExec(new Runnable(){

			
			public void run() {
				if (viewer.getControl()!=null){
					viewer.getControl().redraw();	
				}
			}
			
		});
		
	}

	private void viewerRefresh() {
		// startRefresh();
		try{
		if (viewer!=null){
			viewer.getControl().setRedraw(false);
			try{
			this.viewer.refresh();
			}finally{
				viewer.getControl().setRedraw(true);	
			}
		}
		}catch (Exception e) {
			//TODO: handle exception
		}
		// endRefresh();
	}

	private void endRefresh() {
		try {
			selector.setIgnoreChanges(false);
			if (selection != null) {
				Object[] array = selection.toArray();
				TreePath[] ps = mapPathes(array);
				TreePath[] mapPathes = mapPathes(ep);
				for (int a = 0; a < mapPathes.length; a++) {
					int i = mapPathes[a].getSegmentCount() - 1;
					if (i > 0) {
						Object[] psa = new Object[i];
						for (int b = 0; b < i; b++) {
							psa[b] = mapPathes[a].getSegment(b + 1);
						}
						mapPathes[a] = new TreePath(psa);
					}

				}
				if (mapPathes.length > 0) {
					viewer.setExpandedTreePaths(mapPathes);
				}
				final TreeSelection selectionA = new TreeSelection(ps);
				viewer.getControl().getDisplay().asyncExec(new Runnable() {

					public void run() {

						viewer.setSelection(selectionA, true);
					}

				});
			}
		} finally {
			selection = null;
			ep = null;
		}
	}

	private TreePath[] mapPathes(Object[] array) {
		ArrayList<TreePath> pathes = new ArrayList<TreePath>();
		if (array != null) {
			for (Object o : array) {
				if (o instanceof ITreeNode<?>) {
					ITreeNode<?> m = (ITreeNode<?>) o;
					o = m.getElement();
					if (o instanceof IClusterizationPoint<?>) {
						IClusterizationPoint<?> c = (IClusterizationPoint<?>) o;
						Object primaryValue = c.getPrimaryValue();
						if (primaryValue != null) {
							o = primaryValue;
						}
					}
				}
				List<?> path = node.findPath(null, o);
				if (path != null) {
					TreePath ps = new TreePath(path.toArray());
					pathes.add(ps);
				}
			}
		}
		TreePath[] ps = new TreePath[pathes.size()];
		pathes.toArray(ps);
		viewer.setChildCount(node, getChildren(node).length);
		for (TreePath o : pathes) {
			ITreeNode<?> prev = null;
			for (int a = 0; a < o.getSegmentCount(); a++) {
				ITreeNode<?> n = (ITreeNode<?>) o.getSegment(a);
				if (prev != null) {
					ITreeNode<?>[] children = getChildren(prev);
					int indexOf = Arrays.asList(children).indexOf(n);
					if (indexOf != -1) {
						final int i = children.length - indexOf - 1;
						if (i < 0) {
							continue;
						}
						viewer.setChildCount(prev, children.length);
						updateElement(prev, this.inverse ? indexOf : i);
					}

				} else {

					// updateElement(prev,this.inverse? indexOf:i);
				}
				prev = n;
			}
		}
		return ps;
	}

	private void startRefresh() {
		if (viewer.getTree().getItemCount() > 0) {
			selection = (StructuredSelection) this.viewer.getSelection();
		}
		this.ep = this.viewer.getExpandedElements();
		selector.setIgnoreChanges(true);
	}

	public void addFilter(IFilter flt) {
		if (this.filters == null) {
			this.filters = new HashSet<IFilter>();
		}
		flt.addFilterListener(this);
		this.filters.add(flt);
		this.sortedMap.clear();
		this.refreshViewer();
	}

	public void removeFilter(IFilter flt) {
		flt.removeFilterListener(this);
		if (this.filters != null) {
			this.filters.remove(flt);
			if (this.filters.isEmpty()) {
				this.filters = null;
			}
		}
		this.sortedMap.clear();
		this.refreshViewer();
	}

	private void refreshViewer() {
		viewerRefresh();
	}

	@SuppressWarnings("unchecked")
	public void changed(Object extraData) {
		if (extraData instanceof ISetDelta<?>) {
			final ISetDelta<Object> cm = (ISetDelta<Object>) extraData;
			final Collection<?> changedElements = cm.getChangedElements();
			final Collection<?> rmElements = cm.getRemovedElements();
			final Collection<?> aElements = cm.getAddedElements();
			if (changedElements.size() + rmElements.size() < this.sortedMap
					.size() / 10) {
				for (final Object o : changedElements) {
					this.clearFromCache(o);
				}
				for (final Object o : rmElements) {
					this.clearFromCache(o);
				}
				for (final Object o : aElements) {
					this.clearFromCache(o);
				}
			} else {
				this.sortedMap.clear();
			}
		}
	}

	private void clearFromCache(Object o) {
		this.sortedMap.remove(o);
	}

	public void changed(IFilter provider, Object extraData) {
		this.sortedMap.clear();
		this.refreshViewer();
	}

	public void addListener(
			ISimpleChangeListener<IRealmContentProvider> listener) {
		this.listeners.add(listener);
	}

	public void removeContentListener(
			ISimpleChangeListener<IRealmContentProvider> listener) {
		this.listeners.remove(listener);
	}

	public void processTreeChange(ITreeNode<Object> parentElement,
			ISetDelta<ITreeNode<Object>> dlt) {
		this.viewer.getControl().setRedraw(false);
		this.viewer.getControl().setCursor(
				this.viewer.getControl().getDisplay().getSystemCursor(
						SWT.CURSOR_WAIT));
		this.changed(dlt);
		for (final ISimpleChangeListener<IRealmContentProvider> cp : this.listeners) {
			cp.changed(this, dlt);
		}
		viewerRefresh();
		this.viewer.getControl().setRedraw(true);
		this.viewer.getControl().setCursor(null);
	}

	public void processUnknownTreeChange(ITreeNode<Object> parentElement) {
		viewerRefresh();
	}

	public ITreeNode<?> getChild(ITreeNode<?> node2, int index) {
		final ITreeNode<?>[] childrenSorted = this.getChildrenSorted(node2);
		return childrenSorted[this.inverse ? index : childrenSorted.length
				- index - 1];
	}

	@SuppressWarnings("unchecked")
	public ITreeNode[] getChildrenSorted(ITreeNode parentNode) {
		if (parentNode == null) {
			return new ITreeNode[0];
		}
		if (!this.viewer.getExpandedState(parentNode)) {
			final Object input = this.viewer.getInput();
			if (!parentNode.equals(input)) {
				return new ITreeNode[0];
			}
		}
		final ITreeNode<?>[] children = this.getChildren(parentNode);
		if (children == null) {
			return new ITreeNode[0];
		}
		if (this.inverse) {
			final ITreeNode<?>[] newChildren = new ITreeNode[children.length];
			for (int a = 0; a < children.length; a++) {
				newChildren[children.length - a - 1] = children[a];
			}
		}
		return children;
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> getContents() {
		return (Collection) Arrays.asList(this.node.getChildren());
	}

	@SuppressWarnings("unchecked")
	public ITreeNode getPrev(ITreeNode item) {
		final ITreeNode it = this.getPrev0(item);
		if (it != null) {
			final ITreeNode[] items = this.getChildrenSorted(it);
			if ((items != null) && (items.length > 0)) {
				if ((items[0] != item) && (items[items.length - 1] != item)) {
					return items[this.inverse ? items.length - 1 : 0];
				}
			}
			if (it.equals(this.node)) {
				return null;
			}
		}
		return it;
	}

	@SuppressWarnings("unchecked")
	private ITreeNode getPrev0(ITreeNode item) {
		final ITreeNode is = item.getParentNode();
		final RealmLazyTreeContentProvider treeContentProvider = this;
		if (is != null) {
			final ITreeNode[] items = treeContentProvider.getChildrenSorted(is);
			int indexOf = Arrays.asList(items).indexOf(item);
			final int id = indexOf - this.step();
			if (id == -1) {
				return is;
			}
			if (id == items.length) {
				return is;
			}
			return items[id];
		}
		final ITreeNode[] items = treeContentProvider.getChildrenSorted(item
				.getParentNode());
		final int id = Arrays.asList(items).indexOf(item) - this.step();
		if (id >= 0) {
			return items[id];
		}
		return null;
	}

	private int step() {
		return (this.inverse ? 1 : -1);
	}

	@SuppressWarnings("unchecked")
	public ITreeNode getNext(ITreeNode treeItem2) {
		final ITreeNode[] childs = this.getChildrenSorted(treeItem2);
		if (childs != null) {
			if (childs.length > 0) {
				return this.inverse ? childs[0] : childs[childs.length - 1];
			}
		}
		return this.internalGetNext(treeItem2);
	}

	public boolean isInverse() {
		return this.inverse;
	}

	@SuppressWarnings("unchecked")
	ITreeNode internalGetNext(ITreeNode treeItem2) {
		final ITreeNode is = treeItem2.getParentNode();
		final ITreeNode result = null;
		if (is != null) {
			final ITreeNode[] items = this.getChildrenSorted(is);
			final int id = Arrays.asList(items).indexOf(treeItem2)
					+ this.step();
			if ((id < items.length) && (id != -1)) {
				return items[id];
			} else {
				return this.internalGetNext(is);
			}
		} else {
			final ITreeNode[] items = this.getChildrenSorted(treeItem2
					.getParentNode());
			final int id = Arrays.asList(items).indexOf(treeItem2)
					+ this.step();
			if ((id < items.length) && (id >= 0)) {
				return items[id];
			}
		}
		return result;
	}

	public void restoreSelection(StructuredSelection sel) {
		this.selection = sel;
		endRefresh();
	}

	public void refresh() {
		refreshViewer();
	}
}
