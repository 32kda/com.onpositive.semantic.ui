package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.onpositive.semantic.model.ui.property.editors.structured.UniversalLabelProvider;

public class DefferedManager {

	private static final int FILTERING_LIMIT = 45000;
	protected int limit = 2000;

	public static void clearCache(StructuredViewer vs) {
		if (vs != null) {
			final IBaseLabelProvider labelProvider = vs.getLabelProvider();
			if (labelProvider instanceof UniversalLabelProvider) {
				final UniversalLabelProvider<?> lp = (UniversalLabelProvider<?>) labelProvider;
				lp.clearCache();
			}
		}
	}

	protected static void clearDeffered(Viewer vd, Collection<?> dfs) {
		thr = Thread.currentThread();

		final ArrayList<DefferedUpdate> toRemove = new ArrayList<DefferedUpdate>();
		for (final DefferedUpdate u : new ArrayList<DefferedUpdate>(updates)) {
			if (u.viewer == vd) {
				if (dfs == null) {
					toRemove.add(u);
				} else {
					u.elements.removeAll(dfs);
				}
			}
		}
		if (dfs == null) {
			if ((remove != null) && (remove.viewer == vd)) {
				remove.viewer = null;
			}
		} else {
			if ((remove != null) && (remove.viewer == vd)) {
				remove.elements.removeAll(dfs);
			}
		}
		updates.removeAll(toRemove);
		thr = null;
	}

	protected static Vector<DefferedUpdate> updates = new Vector<DefferedUpdate>();
	protected static volatile Thread thr;
	protected static DefferedUpdate remove;
	protected static Thread updaterThread = new Thread() {

		public void run() {
			while (true) {
				this.checkUpdates();
				try {
					sleep(50);
				} catch (final InterruptedException e) {
					break;
				}
			}
		}

		private void checkUpdates() {
			if (updates.isEmpty()) {
				return;
			}
			try {
				remove = updates.remove(0);
			} catch (final ArrayIndexOutOfBoundsException e) {
				return;
			}
			final StructuredViewer vs = remove.viewer;
			final Control control = vs.getControl();
			if (control.isDisposed()) {
				return;
			}
			final Display display = control.getDisplay();
			if (!updates.isEmpty()) {
				display.syncExec(new Runnable() {

					public void run() {
						if (remove == null) {
							return;
						}
						final StructuredViewer vs = remove.viewer;
						if (!control.isDisposed()) {
							final Display display = control.getDisplay();

							if (display.getThread() == thr) {
								updates.add(remove);
								return;
							}
							if (vs == null) {
								return;
							}
							final Control control = vs.getControl();

							if (!control.isDisposed()) {
								control.setRedraw(false);
								final Object[] array = remove.elements.toArray();
								if (remove.kind == 0) {
									final long l0 = System.currentTimeMillis();
									if (vs instanceof AbstractTreeViewer) {
										((AbstractTreeViewer) vs).add(
												remove.parent, array);
									} else {
										((AbstractTableViewer) vs).add(array);
									}
									final long l1 = System.currentTimeMillis();
									System.out.println(l1 - l0);
								} else if (remove.kind == 1) {

									vs.update(array, null);
								} else if (remove.kind == 2) {

									if (vs instanceof AbstractTreeViewer) {
										((AbstractTreeViewer) vs).remove(array);
									} else {
										((AbstractTableViewer) vs)
												.remove(array);
									}
								}
								control.setRedraw(true);
							}
						}
					}
				}

				);
			}
		}
	};

	protected Object[] split(StructuredViewer vs, int op, Object[] children,
			Object parent) {
		if (children.length > this.limit) {
			final long l0 = System.currentTimeMillis();
			final ViewerFilter[] fl = vs.getFilters();
			final ArrayList<Object> ms = new ArrayList<Object>(children.length);
			if (children.length < FILTERING_LIMIT) {
				l2: for (final Object o : children) {
					for (final ViewerFilter f : fl) {
						if (!f.select(vs, parent, o)) {
							continue l2;
						}
					}
					ms.add(o);
				}
				children = ms.toArray();
				if (children.length < this.limit) {
					return children;
				}
			}
			final long l1 = System.currentTimeMillis();
			System.out.println("Filtering:" + (l1 - l0)); //$NON-NLS-1$
			final Object[] result = new Object[this.limit];
			System.arraycopy(children, 0, result, 0, this.limit);
			int b = 0;
			DefferedUpdate update = new DefferedUpdate(DefferedUpdate.ADD, vs,
					parent);
			for (int a = this.limit; a < children.length; a++) {
				update.elements.add(children[a]);
				b++;
				if (b == this.limit) {
					update.shedule();
					update = new DefferedUpdate(DefferedUpdate.ADD, vs, parent);
					b = 0;
				}
			}

			return result;
		}
		return children;
	}

	public static class DefferedUpdate {

		public static final int ADD = 0;
		public static final int CHANGE = 1;
		public static final int REMOVE = 2;
		protected StructuredViewer viewer;
		Object parent;

		int kind;

		public HashSet<Object> elements = new HashSet<Object>();

		public DefferedUpdate(int kind, StructuredViewer viewer2,
				Object parentElement) {
			this.viewer = viewer2;
			this.kind = kind;
			this.parent = parentElement;
		}

		public void shedule() {
			updates.add(this);
		}

	}

	public DefferedManager() {
		super();
	}

	static {
		updaterThread.setDaemon(true);
		updaterThread.start();
	}

	protected static void ensureCreated(Viewer vd, Collection<?> dfs) {
		clearDeffered(vd, dfs);

		if (vd instanceof AbstractTreeViewer) {

		} else if (vd instanceof AbstractTableViewer) {
			final AbstractTableViewer ta = (AbstractTableViewer) vd;
			final Table ts = (Table) ta.getControl();
			for (final TableItem t : ts.getItems()) {
				dfs.remove(t.getData());
			}
			ta.add(dfs.toArray());
		}
	}

	public void ensureAllCreated(Viewer vs, Object value) {
		if (value instanceof Object[]) {
			ensureCreated(vs, new HashSet<Object>(Arrays.asList(value)));
		}
		if (value instanceof Collection) {
			ensureCreated(vs, new HashSet<Object>((Collection<?>) value));
		}
	}
}