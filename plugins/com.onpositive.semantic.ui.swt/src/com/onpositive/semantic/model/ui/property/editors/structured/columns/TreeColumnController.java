package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.util.Arrays;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class TreeColumnController extends AbstractController {

	private final Tree tree;
	private final TreeViewer viewer;

	public TreeColumnController(TreeViewer viewer, TreeColumn item,
			Layout layout) {
		super(new TreeViewerColumn(viewer, item), item, layout);
		this.tree = item.getParent();

		this.viewer = viewer;
	}

	public void setMovable(boolean movable) {
		final TreeColumn ti = (TreeColumn) this.item;
		ti.setMoveable(movable);
	}

	public void setResizable(boolean resizable) {
		final TreeColumn ti = (TreeColumn) this.item;
		ti.setResizable(resizable);
	}

	public void setTooltipText(String tooltip) {
		final TreeColumn ti = (TreeColumn) this.item;
		ti.setToolTipText(tooltip);
	}

	public void setLayoutData(ColumnLayoutData ld) {
		if (this.layout != null) {
			final TreeColumnLayout la = (TreeColumnLayout) this.layout;
			la.setColumnData(this.item, ld);
		}
	}

	protected void setSortColumn(boolean up) {
		if (this.item.getImage() == null) {
			this.tree.setSortDirection(up ? SWT.UP : SWT.DOWN);
			this.tree.setSortColumn((TreeColumn) this.item);
		} else {
			this.tree.setSortColumn(null);
		}
	}

	public ColumnViewer getViewer() {
		return this.viewer;
	}

	
	protected int getIndex() {
		return Arrays.asList(tree.getColumns()).indexOf(this.item);
	}

}