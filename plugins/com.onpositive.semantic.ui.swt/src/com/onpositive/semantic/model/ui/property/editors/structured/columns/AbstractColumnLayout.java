package com.onpositive.semantic.model.ui.property.editors.structured.columns;

/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation (original file org.eclipse.ui.texteditor.templates.ColumnLayout)
 *     Tom Schindl <tom.schindl@bestsolution.at> - refactored to be widget independent (bug 171824)
 *                                               - fix for bug 178280, 184342, 184045
 *******************************************************************************/

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Widget;

/**
 * The AbstractColumnLayout is a {@link Layout} used to set the size of a table
 * in a consistent way even during a resize unlike a {@link TableLayout} which
 * only sets initial sizes.
 * 
 * <p>
 * <b>You can only add the layout to a container whose only child is the
 * table/tree control you want the layouts applied to.</b>
 * </p>
 * 
 * @since 3.3
 */
abstract class AbstractColumnLayout extends Layout {
	/**
	 * The number of extra pixels taken as horizontal trim by the table column.
	 * To ensure there are N pixels available for the content of the column,
	 * assign N+COLUMN_TRIM for the column width.
	 * 
	 * @since 3.1
	 */
	private static int COLUMN_TRIM = "carbon".equals(SWT.getPlatform()) ? 24 : 3; //$NON-NLS-1$

	static final boolean IS_GTK = "gtk".equals(SWT.getPlatform());//$NON-NLS-1$

	static final String LAYOUT_DATA = Policy.JFACE + ".LAYOUT_DATA"; //$NON-NLS-1$

	private boolean inupdateMode = false;

	private boolean relayout = true;

	private final Listener resizeListener = new Listener() {

		public void handleEvent(Event event) {
			if (!AbstractColumnLayout.this.inupdateMode) {
				AbstractColumnLayout.this.updateColumnData(event.widget);
			}
		}

	};

	/**
	 * Adds a new column of data to this table layout.
	 * 
	 * @param column
	 *            the column
	 * 
	 * @param data
	 *            the column layout data
	 */
	public void setColumnData(Widget column, ColumnLayoutData data) {

		if (column.getData(LAYOUT_DATA) == null) {
			column.addListener(SWT.Resize, this.resizeListener);
		}

		column.setData(LAYOUT_DATA, data);
	}

	/**
	 * Compute the size of the table or tree based on the ColumnLayoutData and
	 * the width and height hint.
	 * 
	 * @param scrollable
	 *            the widget to compute
	 * @param wHint
	 *            the width hint
	 * @param hHint
	 *            the height hint
	 * @return Point where x is the width and y is the height
	 */
	private Point computeTableTreeSize(Scrollable scrollable, int wHint,
			int hHint) {
		final Point result = scrollable.computeSize(wHint, hHint);

		int width = 0;
		final int size = this.getColumnCount(scrollable);
		for (int i = 0; i < size; ++i) {
			final ColumnLayoutData layoutData = this.getLayoutData(scrollable,
					i);
			if (layoutData instanceof ColumnPixelData) {
				final ColumnPixelData col = (ColumnPixelData) layoutData;
				width += col.width;
				if (col.addTrim) {
					width += COLUMN_TRIM;
				}
			} else if (layoutData instanceof ColumnWeightData) {
				final ColumnWeightData col = (ColumnWeightData) layoutData;
				width += col.minimumWidth;
			} else {
				Assert.isTrue(false, "Unknown column layout data"); //$NON-NLS-1$
			}
		}
		if (width > result.x) {
			result.x = width;
		}

		return result;
	}

	/**
	 * Layout the scrollable based on the supplied width and area. Only increase
	 * the size of the scrollable if increase is <code>true</code>.
	 * 
	 * @param scrollable
	 * @param width
	 * @param area
	 * @param increase
	 */
	private void layoutTableTree(final Scrollable scrollable, final int width,
			final Rectangle area, final boolean increase) {
		final int size = this.getColumnCount(scrollable);
		final int[] widths = new int[size];

		final int[] weightIteration = new int[size];
		int numberOfWeightColumns = 0;

		int fixedWidth = 0;
		int minWeightWidth = 0;
		int totalWeight = 0;

		// First calc space occupied by fixed columns
		for (int i = 0; i < size; i++) {
			final ColumnLayoutData col = this.getLayoutData(scrollable, i);
			if (col instanceof ColumnPixelData) {
				final ColumnPixelData cpd = (ColumnPixelData) col;
				int pixels = cpd.width;
				if (cpd.addTrim) {
					pixels += COLUMN_TRIM;
				}
				widths[i] = pixels;
				fixedWidth += pixels;
			} else if (col instanceof ColumnWeightData) {
				final ColumnWeightData cw = (ColumnWeightData) col;
				weightIteration[numberOfWeightColumns] = i;
				numberOfWeightColumns++;
				totalWeight += cw.weight;
				minWeightWidth += cw.minimumWidth;
				widths[i] = cw.minimumWidth;
			} else {
				Assert.isTrue(false, "Unknown column layout data"); //$NON-NLS-1$
			}
		}

		// Do we have columns that have a weight?
		final int restIncludingMinWidths = width - fixedWidth;
		final int rest = restIncludingMinWidths - minWeightWidth;
		if ((numberOfWeightColumns > 0) && (rest > 0)) {

			// Modify the weights to reflect what each column already
			// has due to its minimum. Otherwise, columns with low
			// minimums get discriminated.
			int totalWantedPixels = 0;
			final int[] wantedPixels = new int[numberOfWeightColumns];
			for (int i = 0; i < numberOfWeightColumns; i++) {
				final ColumnWeightData cw = (ColumnWeightData) this
						.getLayoutData(scrollable, weightIteration[i]);
				wantedPixels[i] = totalWeight == 0 ? 0 : cw.weight
						* restIncludingMinWidths / totalWeight;
				totalWantedPixels += wantedPixels[i];
			}

			// Now distribute the rest to the columns with weight.
			int totalDistributed = 0;
			for (int i = 0; i < numberOfWeightColumns; ++i) {
				final int pixels = totalWantedPixels == 0 ? 0 : wantedPixels[i]
						* rest / totalWantedPixels;
				totalDistributed += pixels;
				widths[weightIteration[i]] += pixels;
			}

			// Distribute any remaining pixels to columns with weight.
			int diff = rest - totalDistributed;
			for (int i = 0; diff > 0; i = ((i + 1) % numberOfWeightColumns)) {
				++widths[weightIteration[i]];
				--diff;
			}
		}

		if (increase) {
			scrollable.setSize(area.width - 2, area.height - 2);
		}

		this.inupdateMode = true;
		this.setColumnWidths(scrollable, widths);
		scrollable.update();
		this.inupdateMode = false;

		if (!increase) {
			scrollable.setSize(area.width - 2, area.height - 2);
		}
		scrollable.setLocation(1, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite
	 * , int, int, boolean)
	 */
	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		return this.computeTableTreeSize(this.getControl(composite), wHint,
				hHint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite,
	 * boolean)
	 */
	protected void layout(Composite composite, boolean flushCache) {
		final Rectangle area = composite.getClientArea();
		final Scrollable table = this.getControl(composite);
		final int tableWidth = table.getSize().x;
		final int trim = this.computeTrim(area, table, tableWidth);
		final int width = Math.max(0, area.width - trim);

		if (width > 1) {
			this.layoutTableTree(table, width, area, tableWidth < area.width);
		}

		// For the first time we need to relayout because Scrollbars are not
		// calculate appropriately
		if (this.relayout) {
			this.relayout = false;
			composite.layout();
		}
	}

	/**
	 * Compute the area required for trim.
	 * 
	 * @param area
	 * @param scrollable
	 * @param currentWidth
	 * @return int
	 */
	private int computeTrim(Rectangle area, Scrollable scrollable,
			int currentWidth) {
		int trim;

		if (currentWidth > 1) {
			trim = currentWidth - scrollable.getClientArea().width + 3;
		} else {
			// initially, the table has no extend and no client area - use the
			// border with
			// plus some padding as educated guess
			trim = 2 * scrollable.getBorderWidth() + 3;
		}

		return trim;
	}

	/**
	 * Get the control being laid out.
	 * 
	 * @param composite
	 *            the composite with the layout
	 * @return {@link Scrollable}
	 */
	Scrollable getControl(Composite composite) {
		return (Scrollable) composite.getChildren()[0];
	}

	/**
	 * Get the number of columns for the receiver.
	 * 
	 * @return the number of columns
	 */
	abstract int getColumnCount(Scrollable tableTree);

	/**
	 * Set the widths of the columns.
	 * 
	 * @param widths
	 */
	abstract void setColumnWidths(Scrollable tableTree, int[] widths);

	abstract ColumnLayoutData getLayoutData(Scrollable tableTree,
			int columnIndex);

	abstract void updateColumnData(Widget column);
}