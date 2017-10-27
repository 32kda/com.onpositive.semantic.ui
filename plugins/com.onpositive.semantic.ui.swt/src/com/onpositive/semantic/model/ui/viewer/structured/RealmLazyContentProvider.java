package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.realm.IFilter;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ISimpleChangeListener;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.property.editors.structured.IRealmContentProvider;

public class RealmLazyContentProvider implements ILazyContentProvider,
		IRealmChangeListener<Object>, IRealmContentProvider,
		ISimpleChangeListener<IFilter> {

	private TableViewer tv;
	private IRealm<?> input;
	private Object[] contents;
	private Comparator<Object> comparator;
	private HashSet<IFilter> filters;
	private final HashSet<ISimpleChangeListener<IRealmContentProvider>> listeners = new HashSet<ISimpleChangeListener<IRealmContentProvider>>();
	private boolean inverse;

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public RealmLazyContentProvider() {

	}

	public void updateElement(int index) {
		try {
			if (this.contents.length <= index) {
				return;
			}
			if (this.inverse) {
				this.tv.replace(this.contents[index], index);
			} else {
				Object element = this.contents[this.contents.length - index - 1];
				if (element != null) {
					this.tv.replace(element, index);
				}
			}
		} catch (Exception e) {
			Platform.log(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void dispose() {
		if (this.input != null) {
			this.input.removeRealmChangeListener((IRealmChangeListener) this);
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.tv = (TableViewer) viewer;
		if (this.input != null) {
			this.input.removeRealmChangeListener((IRealmChangeListener) this);
		}
		this.input = (IRealm<?>) newInput;
		if (this.input != null) {
			this.input.addRealmChangeListener((IRealmChangeListener) this);
		}
		this.refreshViewer();
	}

	public void realmChanged(IRealm<Object> realmn, ISetDelta<Object> delta) {
		final Collection<Object> addedElements = delta.getAddedElements();
		final Collection<Object> removedElements = delta.getRemovedElements();
		this.fireChanged(delta);
		if ((addedElements.size() != 0) || (removedElements.size() != 0)) {
			this.refreshViewer();
		}
		if ((delta.getChangedElements().size() > 0) && (this.filters != null)
				&& (this.filters.size() > 0)) {
			this.refreshViewer();
		} else {
			enableSorting();
			this.tv.refresh();
		}

	}

	private void fireChanged(ISetDelta<Object> delta) {
		for (final ISimpleChangeListener<IRealmContentProvider> listener : this.listeners) {
			listener.changed(this, delta);
		}
	}

	private void refreshViewer() {
		if (this.input == null) {
			return;
		}
		final ISelection selection = this.tv.getSelection();
		final long l0 = System.currentTimeMillis();
		final Collection<?> contents2 = this.input.getContents();
		if (this.filters != null) {
			final ArrayList<Object> filtered = new ArrayList<Object>();
			l2: for (final Object o : contents2) {
				for (final IFilter f : this.filters) {
					if (!f.accept(o)) {
						continue l2;
					}
				}
				filtered.add(o);
			}
			this.contents = filtered.toArray();
		} else {
			this.contents = contents2.toArray();
		}
		enableSorting();
		this.tv.setItemCount(this.contents.length);
		this.tv.refresh();
		this.tv.setSelection(selection);
		final long l1 = System.currentTimeMillis();
		System.out.println(l1 - l0);
	}

	protected void enableSorting() {
		if (this.comparator != null) {
			Arrays.sort(this.contents, this.comparator);
		}
	}

	@SuppressWarnings("unchecked")
	public void setSortOrder(Comparator realComparator) {
		this.comparator = realComparator;
		this.refreshViewer();
	}

	@SuppressWarnings("unchecked")
	public void setComparator(Comparator<? extends Object> cmp, boolean inverse) {
		if (cmp == this.comparator) {
			this.inverse = inverse;
			if (tv != null) {
				this.tv.refresh();
			}
		} else {
			this.comparator = (Comparator<Object>) cmp;
			this.inverse = inverse;
			this.refreshViewer();
		}
	}

	public void addFilter(com.onpositive.semantic.model.realm.IFilter flt) {
		if (this.filters == null) {
			this.filters = new HashSet<IFilter>();
		}
		flt.addFilterListener(this);
		this.filters.add(flt);
		this.refreshViewer();
	}

	public void removeFilter(com.onpositive.semantic.model.realm.IFilter flt) {
		flt.removeFilterListener(this);
		if (this.filters != null) {
			this.filters.remove(flt);
			if (this.filters.isEmpty()) {
				this.filters = null;
			}
		}
		this.refreshViewer();
	}

	public void changed(IFilter provider, Object extraData) {
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

	public Collection<Object> getContents() {
		return Arrays.asList(this.contents);
	}

	public void restoreSelection(StructuredSelection sel) {
		this.tv.setItemCount(this.contents.length);
		HashSet<Object> sm = new HashSet<Object>();
		for (Object o : sel.toArray()) {
			if (o instanceof ITreeNode<?>) {
				ITreeNode<?> m = (ITreeNode<?>) o;
				o = m.getElement();
				if (o instanceof IClusterizationPoint<?>) {
					IClusterizationPoint<?> l = (IClusterizationPoint<?>) o;
					Object primaryValue = l.getPrimaryValue();
					if (primaryValue != null) {
						o = primaryValue;
					}
				}
			}

			sm.add(o);
			int indexOf = Arrays.asList(contents).indexOf(o);
			if (this.inverse) {
				this.tv.replace(this.contents[indexOf], indexOf);
				// this.tv.getTable().select(indexOf);
			} else {
				if (indexOf != -1) {
					this.tv.replace(this.contents[this.contents.length
							- indexOf - 1], indexOf);
				}
				// this.tv.getTable().select(this.contents.length - indexOf -
				// 1);
			}

		}
		this.tv.setSelection(new StructuredSelection(sm.toArray()), true);
		// tv.setSelection(sel);
	}

	public void refresh() {
		refreshViewer();
	}
}