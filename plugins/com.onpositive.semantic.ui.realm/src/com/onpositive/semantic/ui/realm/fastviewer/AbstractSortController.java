package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.Comparator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

/**
 * 
 */
public abstract class AbstractSortController extends SelectionAdapter {

	private final class MComparator implements Comparator {
		private final boolean state;

		private MComparator(boolean state) {
			this.state = state;
		}

		public int compare(Object e1, Object e2) {
			int rev = state ? 1 : -1;
			return rev * columnComparator.compare(e1, e2);
		}
	}

	private static final String CONTROLLER = "controller";
	private StructuredViewer viewer;
	private Item column;
	private Comparator columnComparator;
	private boolean state = true;

	// private static WeakHashMap instantes = new WeakHashMap();

	public static AbstractSortController getController(Control control,
			Item column) {
		return (AbstractSortController) column.getData(CONTROLLER);
	}

	public AbstractSortController(StructuredViewer viewer, Item column,
			Comparator columnComparator, boolean defaultDesc) {
		column.setData(CONTROLLER, this);
		this.viewer = viewer;
		this.column = column;
		this.columnComparator = columnComparator;
		this.state = defaultDesc;
	}

	public void widgetSelected(SelectionEvent e) {
		boolean newColumn = false;
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer) viewer;
			newColumn = tv.getTable().getSortColumn() != column;
		}
		if (viewer instanceof TreeViewer) {
			TreeViewer tv = (TreeViewer) viewer;
			newColumn = tv.getTree().getSortColumn() != column;
		}
		if (newColumn) {
			this.state = true;
		}
		stateChanged();
		//if (!newColumn) {
			this.state = !state;
		//}
	}

	public void setState(boolean state) {
		stateChanged();
		this.state = !state;
	}

	private boolean getState() {
		return state;
	}

	protected StructuredViewer getViewer() {
		return viewer;
	}

	protected Item getColumn() {
		return column;
	}

	private ViewerSorter getViewerSorter() {
		final boolean state = getState();
		return new ViewerSorter() {

			public int compare(Viewer viewer, Object e1, Object e2) {
				int rev = state ? 1 : -1;
				return rev * columnComparator.compare(e1, e2);
			}

		};
	}

	public Comparator getComparator() {
		final boolean state = getState();
		return new MComparator(state);
	}

	protected int getSortDirection() {
		return getState() ? SWT.DOWN : SWT.UP;
	}

	public void initState() {
		state = getRealSortDirection() == SWT.DOWN;
		update();
		this.state = !state;
	}

	protected void update() {
		getViewer().setSorter(getViewerSorter());
	}

	public abstract int getRealSortDirection();

	public abstract void stateChanged();

}
