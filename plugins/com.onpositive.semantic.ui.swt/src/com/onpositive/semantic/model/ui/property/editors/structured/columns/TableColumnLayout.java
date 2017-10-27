package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

public class TableColumnLayout extends AbstractColumnLayout {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.layout.AbstractColumnLayout#getColumnCount(org.eclipse
	 * .swt.widgets.Scrollable)
	 */
	int getColumnCount(Scrollable tableTree) {
		return ((Table) tableTree).getColumnCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.layout.AbstractColumnLayout#setColumnWidths(org.eclipse
	 * .swt.widgets.Scrollable, int[])
	 */
	void setColumnWidths(Scrollable tableTree, int[] widths) {
		final TableColumn[] columns = ((Table) tableTree).getColumns();
		for (int i = 0; i < widths.length; i++) {
			columns[i].setWidth(widths[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.layout.AbstractColumnLayout#getLayoutData(int)
	 */
	ColumnLayoutData getLayoutData(Scrollable tableTree, int columnIndex) {
		final TableColumn column = ((Table) tableTree).getColumn(columnIndex);
		return (ColumnLayoutData) column.getData(LAYOUT_DATA);
	}

	Composite getComposite(Widget column) {
		return ((TableColumn) column).getParent().getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.layout.AbstractColumnLayout#updateColumnData(org.eclipse
	 * .swt.widgets.Widget)
	 */
	void updateColumnData(Widget column) {
		final TableColumn tColumn = (TableColumn) column;
		final Table t = tColumn.getParent();

		if (!IS_GTK || (t.getColumn(t.getColumnCount() - 1) != tColumn)) {
			final int width = tColumn.getWidth();
			if (width != 0) {
				tColumn.setData(LAYOUT_DATA, new ColumnPixelData(width));
				this.layout(t.getParent(), true);
			}
		}
	}
}
