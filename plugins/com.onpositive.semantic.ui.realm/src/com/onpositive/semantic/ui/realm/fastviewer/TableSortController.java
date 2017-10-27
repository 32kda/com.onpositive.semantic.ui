/**
 * 
 */
package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

public class TableSortController extends AbstractSortController {

	/**
	 * @param viewer
	 * @param column
	 * @param columnComparator
	 * @param defaultDesc - true, if default sorting whould be from
	 *  the maximum value to the minimum value 
	 */
	public TableSortController(TableViewer viewer, TableColumn column,
			Comparator columnComparator, boolean defaultDesc) {
		super(viewer, column, columnComparator, defaultDesc);
		column.addSelectionListener(this);
	}

	public void stateChanged() {
		TableViewer viewer = (TableViewer)getViewer();
		viewer.getTable().setSortColumn((TableColumn)getColumn());
		viewer.getTable().setSortDirection(getSortDirection());
		update();
	}
	
	public int getRealSortDirection() {
		return ((TableViewer)getViewer()).getTable().getSortDirection();
	}

}