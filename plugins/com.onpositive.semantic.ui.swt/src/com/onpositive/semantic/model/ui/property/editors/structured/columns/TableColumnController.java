package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.util.Arrays;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnController extends AbstractController {

	private final Table table;
	private final TableViewer viewer;

	public TableColumnController(final TableViewer viewer, TableColumn item,
			Layout layout2) {
		super(new TableViewerColumn(viewer, item), item, layout2);
		this.table = item.getParent();
		this.viewer = viewer;
	}

	public void setMovable(boolean movable) {
		final TableColumn ti = (TableColumn) this.item;
		ti.setMoveable(movable);
	}

	public void setResizable(boolean resizable) {
		final TableColumn ti = (TableColumn) this.item;
		ti.setResizable(resizable);
	}

	public void setTooltipText(String tooltip) {
		final TableColumn ti = (TableColumn) this.item;
		ti.setToolTipText(tooltip);
	}

	public void setLayoutData(ColumnLayoutData ld) {
		if (this.layout != null) {
			final TableColumnLayout la = (TableColumnLayout) this.layout;
			la.setColumnData(this.item, ld);
		}
	}

	protected void setSortColumn(boolean up) {
		if (this.item.getImage()==null){
		this.table.setSortDirection(up ? SWT.UP : SWT.DOWN);
		this.table.setSortColumn((TableColumn) this.item);
		}
		else {
			this.table.setSortColumn(null);
		}
	}

	public ColumnViewer getViewer() {
		return this.viewer;
	}

	
	protected int getIndex() {
		return Arrays.asList(table.getColumns()).indexOf(this.item);
	}
}
