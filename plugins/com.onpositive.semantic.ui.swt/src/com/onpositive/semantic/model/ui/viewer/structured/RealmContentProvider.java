package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.Collection;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.property.editors.structured.UIRealm;
import com.onpositive.semantic.model.ui.property.editors.structured.UniversalLabelProvider;

public class RealmContentProvider implements IStructuredContentProvider {

	private IRealm<Object> currentRealm;
	private StructuredViewer vs;

	private final IRealmChangeListener<Object> listener = new IRealmChangeListener<Object>() {

		public void realmChanged(IRealm<Object> realmn, ISetDelta<Object> delta) {
			onChange(delta);
		}

	};

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		final Object[] children = ((IRealm) inputElement).getContents()
				.toArray();
		return children;
	}

	public void dispose() {
		if (this.currentRealm != null) {
			this.currentRealm.removeRealmChangeListener(this.listener);
			this.currentRealm = null;
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.vs = (StructuredViewer) viewer;
		if (this.currentRealm != newInput) {
			if (this.currentRealm != null) {
				this.currentRealm.removeRealmChangeListener(this.listener);
			}
			final IRealm<Object> newInput2 = (IRealm<Object>) newInput;
			if (newInput2 != null) {
				this.currentRealm = UIRealm.toUI(newInput2);
				this.currentRealm.addRealmChangeListener(this.listener);
			} else {
				this.currentRealm = null;
			}
		}
	}

	protected void onChange(ISetDelta<Object> delta)
	{
		final Collection<Object> removedElements = delta
				.getRemovedElements();
		final Object[] removed = removedElements.toArray();
		final ISelection sel = RealmContentProvider.this.vs.getSelection();
		final Object[] added = delta.getAddedElements().toArray();
		final Object[] array = delta.getChangedElements().toArray();
		if (RealmContentProvider.this.vs instanceof ColumnViewer) {
			final ColumnViewer v = (ColumnViewer) RealmContentProvider.this.vs;
			if (v instanceof AbstractTableViewer) {
				final AbstractTableViewer ta = (AbstractTableViewer) v;

				ta.getControl().setRedraw(false);
				ta.add(added);
				int sa = -1;
				final Control cnt = ta.getControl();
				final Table ts = (Table) ((cnt instanceof Table) ? cnt
						: null);
				if ((removed.length > 0) && (ts != null)) {
					if (ts.getSelectionIndices().length == 1) {
						sa = ts.getSelectionIndex();
					}
				}
				ta.remove(removed);
				final IBaseLabelProvider pr = ta.getLabelProvider();
				if (pr instanceof UniversalLabelProvider<?>) {
					final UniversalLabelProvider<?> la = (UniversalLabelProvider<?>) pr;
					for (final Object o : delta.getChangedElements()) {
						la.clearCache(o);
					}
				}
				for (final Object o : array) {
					ta.remove(o);
					ta.add(o);
				}
				RealmContentProvider.this.vs.setSelection(sel);
				if (sa != -1) {
					final int ic = ts.getItemCount();
					if (ic <= sa) {
						sa = ic - 1;
					}
					final Object el = ta.getElementAt(sa);
					if (el != null) {
						ta.setSelection(new StructuredSelection(el), true);
					}

				}
				if (ts != null) {
					if ((added != null) && (added.length == 1)) {
						ta.setSelection(new StructuredSelection(added[0]),
								true);
					}
				}

				ta.getControl().setRedraw(true);

			}
			if (v instanceof AbstractTreeViewer) {
				final AbstractTreeViewer ta = (AbstractTreeViewer) v;
				ta.getControl().setRedraw(false);
				ta.add(null, added);
				ta.remove(removed);
				ta.update(array, null);
				RealmContentProvider.this.vs.setSelection(sel);
				ta.getControl().setRedraw(true);

			}
		} else if (RealmContentProvider.this.vs instanceof AbstractListViewer) {
			final AbstractListViewer ta = (AbstractListViewer) RealmContentProvider.this.vs;
			ta.getControl().setRedraw(false);
			ta.add(added);
			ta.remove(removed);
			ta.update(array, null);
			RealmContentProvider.this.vs.setSelection(sel);
			ta.getControl().setRedraw(true);
		}
	}
}