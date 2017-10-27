package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.tree.ITreeChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;

public class TreeNodeContentProvider extends DefferedManager implements
		ITreeContentProvider, ITreeChangeListener<Object> {

	private ITreeNode<Object> node;
	private AbstractTreeViewer viewer;
	private boolean expandOnAddition = false;
	private final HashSet<ITreeNode<?>> listeners = new HashSet<ITreeNode<?>>();

	public Object[] getChildren(Object parentElement) {
		final ITreeNode<?> el = (ITreeNode<?>) parentElement;
		clearCache(this.viewer);
		final ITreeNode<?>[] children = el.getChildren();
		if (children.length > this.getTreeCacheLimit()) {
			clearDeffered(this.viewer, null);
		} else {
			clearDeffered(this.viewer, Arrays.asList(children));
		}
		return this.split(this.viewer, this.limit, children, parentElement);
	}

	private int getTreeCacheLimit() {
		return this.limit / 4;
	}

	public Object getParent(Object element) {
		final ITreeNode<?> el = (ITreeNode<?>) element;
		return el.getParentNode();
	}

	public boolean hasChildren(Object element) {
		final ITreeNode<?> el = (ITreeNode<?>) element;
		return el.hasChildren();
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		final Object[] children = this.getChildren(inputElement);
		clearCache(this.viewer);

		final ITreeNode<Object> inputElement2 = (ITreeNode<Object>) inputElement;
		this.listeners.add(inputElement2);
		inputElement2.addChangeListener(this);
		return children;
	}

	public void dispose() {
		if (this.node != null) {
			for (final ITreeNode<?> o : this.listeners) {
				o.removeChangeListener(this);
			}
			this.listeners.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		clearCache((StructuredViewer) viewer);
		clearDeffered(viewer, null);
		if (this.node != null) {
			for (final ITreeNode<?> o : this.listeners) {
				o.removeChangeListener(this);
			}
			this.listeners.clear();

		}
		this.node = (ITreeNode<Object>) newInput;
		this.viewer = (AbstractTreeViewer) viewer;
	}

	public void processTreeChange(final ITreeNode<Object> parentElement,
			final ISetDelta<ITreeNode<Object>> dlt) {
		final Display display = this.viewer.getControl().getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				TreeNodeContentProvider.this
						.internalProcess(parentElement, dlt);
			}

		});
	}

	private void internalProcess(ITreeNode<Object> parentElement,
			ISetDelta<ITreeNode<Object>> dlt) {
		this.viewer.getControl().setRedraw(false);
		final ISelection sl = this.viewer.getSelection();
		clearCache(this.viewer);
		this.process(parentElement, dlt);
		this.viewer.setSelection(sl);
		this.viewer.getControl().setRedraw(true);
	}

	@SuppressWarnings("unchecked")
	private void process(ITreeNode<Object> parentElement,
			ISetDelta<ITreeNode<Object>> dlt) {
		final Collection<ITreeNode<Object>> addedElements = dlt
				.getAddedElements();

		this.viewer.add(parentElement, this.split(this.viewer,
				DefferedManager.DefferedUpdate.ADD, addedElements.toArray(),
				parentElement));
		for (final ITreeNode<Object> o : addedElements) {
			this.viewer.expandToLevel(o, AbstractTreeViewer.ALL_LEVELS);
		}
		final Collection<ITreeNode<Object>> removedElements = dlt
				.getRemovedElements();
		for (final ITreeNode<Object> e : removedElements) {
			e.removeChangeListener(this);
		}
		clearDeffered(this.viewer, removedElements);
		this.viewer.remove(parentElement, removedElements.toArray());
		final Object[] array = dlt.getChangedElements().toArray();
		this.viewer.remove(parentElement, array);
		this.viewer.add(parentElement, array);
		// viewer.update(dlt.getChangedElements().toArray(), null);
		for (final ITreeNode<Object> w : dlt.getChangedElements()) {
			final ISetDelta<ITreeNode<Object>> subDelta = (ISetDelta) dlt
					.getSubDelta(w);
			if ((subDelta != null) && !subDelta.isEmpty()) {
				this.process(w, subDelta);
			}
		}
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

	public void processUnknownTreeChange(final ITreeNode<Object> parentElement) {
		this.viewer.getControl().getDisplay().syncExec(new Runnable() {

			public void run() {
				TreeNodeContentProvider.this.viewer.refresh(parentElement);
			}

		});

	}

	public void setExpandOnAddition(boolean expandOnAddition) {
		this.expandOnAddition = expandOnAddition;
	}

	public boolean isExpandOnAddition() {
		return this.expandOnAddition;
	}

}
