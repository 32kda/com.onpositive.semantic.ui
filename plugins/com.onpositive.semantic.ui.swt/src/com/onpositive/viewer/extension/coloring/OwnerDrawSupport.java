/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Copied from JDT UI: org.eclipse.jdt.internal.ui.viewsupport.OwnerDrawSupport.
 * Will be removed again when made API. https://bugs.eclipse.org/bugs/show_bug.cgi?id=196128
 *******************************************************************************/
package com.onpositive.viewer.extension.coloring;

import java.util.Iterator;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.onpositive.semantic.model.api.roles.StyledString;

/**
 * Adding owner draw support to a control
 */
public abstract class OwnerDrawSupport implements Listener {

	public static final String NEEDS_REFRESH = "needs_refresh"; //$NON-NLS-1$

	public static final String CELL_EDITING = "cell editing";

	public static final String CELL_EDITING_DATA = "cell editing_data";

	private TextLayout fTextLayout;
	private final Control fControl;
	private final ColumnViewer viewer;

	public OwnerDrawSupport(Control control, ColumnViewer viewer) {
		this.fControl = control;
		this.viewer = viewer;
		this.fTextLayout = new TextLayout(control.getDisplay());
		this.fTextLayout.setOrientation(Window.getDefaultOrientation());

		control.addListener(SWT.MeasureItem, this);
		control.addListener(SWT.PaintItem, this);
		control.addListener(SWT.EraseItem, this);
		control.addListener(SWT.Dispose, this);
	}

	protected abstract void refreshItem(Item item);

	/**
	 * Return the colored label for the given item.
	 * 
	 * @param item
	 *            the item to return the colored label for
	 * @param index
	 * @return the colored string
	 */
	public abstract StyledString getColoredLabel(Item item, int index);

	/**
	 * Return the color for the given style
	 * 
	 * @param foregroundColorName
	 *            the name of the color
	 * @param display
	 *            the current display
	 * @return the color
	 */
	public abstract Color getColor(String foregroundColorName, Display display);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	public void handleEvent(Event event) {

		if (event.type == SWT.MeasureItem) {

			event.width += 20;
		}

		if (event.type == SWT.PaintItem) {
			this.performPaint(event);
		} else if (event.type == SWT.EraseItem) {
			this.performErase(event);
			event.detail&=~SWT.FOREGROUND;
		} else if (event.type == SWT.Dispose) {
			this.dispose();
		}
	}

	private void removeSelectionInformation(Event event, Widget item) {
		if (this.viewer.isCellEditorActive()) {
			final int index = event.index;
			final GC gc = event.gc;
			Rectangle bounds = null;
			if (item instanceof TableItem) {
				final TableItem titem = (TableItem) item;
				gc.setBackground(titem.getBackground(index));
				gc.setForeground(titem.getForeground(index));
				bounds = titem.getBounds(index);
			} else {
				final TreeItem titem = (TreeItem) item;
				gc.setBackground(titem.getBackground(index));
				gc.setForeground(titem.getForeground(index));
				bounds = titem.getBounds(index);
			}
			final Object data = this.viewer
					.getData(OwnerDrawSupport.CELL_EDITING);
			if ((data != null) && (data instanceof Integer)) {
				if (index == ((Integer) data).intValue()) {
					final Object data2 = this.viewer
							.getData(OwnerDrawSupport.CELL_EDITING_DATA);
					if (data2 == item.getData()) {
						gc.setBackground(gc.getDevice().getSystemColor(
								SWT.COLOR_WIDGET_BACKGROUND));
						gc.fillRectangle(bounds);
					}
				}
				// else{
				// // Color cl=new Color(gc.getDevice(),235,255,245);
				// // gc.setBackground(cl);
				// // gc.fillRectangle(bounds);
				// // cl.dispose();
				// }
			}
			// This is a workaround for an SWT-Bug on WinXP bug 169517
			gc.drawText(" ", bounds.x, bounds.y, false); //$NON-NLS-1$
			event.detail &= ~SWT.SELECTED;

		}
	}

	private void performErase(Event event) {
		event.detail &= ~SWT.FOREGROUND;
		event.detail &= ~SWT.BACKGROUND;
		if ((event.detail & SWT.SELECTED) > 0) {
			;
			// ViewerCell focusCell = getFocusCell();
			//			
			// Assert
			// .isNotNull(row,
			// "Internal structure invalid. Item without associated row is not
			// possible."); //$NON-NLS-1$

			// if (focusCell == null || !cell.equals(focusCell)) {
			this.removeSelectionInformation(event, event.item);
			// } else {
			// markFocusedCell(event, cell);
			// }
		}
	}

	private void performPaint(Event event) {
		final Item item = (Item) event.item;
		final GC gc = event.gc;
		final Object data = item.getData(NEEDS_REFRESH);
		if (data != null) {
			this.refreshItem(item);
		}
		IBaseLabelProvider labelProvider = viewer.getLabelProvider();
		if (labelProvider instanceof IOwnerSupport){
			IOwnerSupport sp=(IOwnerSupport) labelProvider;
			if (sp.doPaint(this,event)){
				return;
			}
		}
		final StyledString coloredLabel = this.getColoredLabel(item,
				event.index);
		final boolean isSelected = (event.detail & SWT.SELECTED) != 0;

		if (item instanceof TreeItem) {
			final TreeItem treeItem = (TreeItem) item;
			Rectangle b = ((TreeItem) item).getBounds(event.index);
			event.gc.setClipping(b);
			final Image image = treeItem.getImage(event.index);
			Rectangle imageBounds = null;
			Rectangle bounds = null;
			if (image != null) {
				imageBounds = treeItem.getImageBounds(event.index);
				this.processImage(image, gc, imageBounds);
				bounds = image.getBounds();
			}

			final Rectangle textBounds = treeItem.getTextBounds(event.index);
			if (imageBounds != null) {
				if (textBounds.x > imageBounds.x - bounds.width) {
					textBounds.x = imageBounds.x + 18;
				}
			}
			final Font font = treeItem.getFont(event.index);
			this.processColoredLabel(coloredLabel, gc, textBounds, isSelected,
					font);

			// Rectangle bounds = treeItem.getBounds();

		} else if (item instanceof TableItem) {
			final TableItem tableItem = (TableItem) item;
			final Image image = tableItem.getImage(event.index);
			if (image != null) {
				this.processImage(image, gc, tableItem
						.getImageBounds(event.index));
			}
			final Rectangle textBounds = tableItem.getTextBounds(event.index);
			final Font font = tableItem.getFont(event.index);
			
			this.processColoredLabel(coloredLabel, gc, textBounds, isSelected,
					font);
			event.detail &= ~SWT.FOCUSED;

			// Rectangle bounds = tableItem.getBounds();
			// if ((event.detail & SWT.FOCUSED) != 0) {
			// gc.drawFocus(bounds.x, bounds.y, bounds.width, bounds.height);
			// }
		}
		final IItemPaintParticipant participant = this
				.getPaintParticipantLabel(item, event.index);
		if (participant != null) {
			participant.paint(event);
		}
	}

	public abstract IItemPaintParticipant getPaintParticipantLabel(Item item,
			int index);

	private void processImage(Image image, GC gc, Rectangle imageBounds) {
		final Rectangle bounds = image.getBounds();
		final int x = imageBounds.x
				+ Math.max(0, (imageBounds.width - bounds.width) / 2);
		final int y = imageBounds.y
				+ Math.max(0, (imageBounds.height - bounds.height) / 2);
		gc.drawImage(image, x, y);
	}

	@SuppressWarnings("unchecked")
	public void processColoredLabel(StyledString richLabel, GC gc,
			Rectangle textBounds, boolean isSelected, Font font) {
		if (richLabel == null) {
			return;
		}
		
		final String text = richLabel.getString();

		this.fTextLayout.setText(text);
		this.fTextLayout.setFont(font);
		// apply the styled ranges only when element is not selected
		final Display display = (Display) gc.getDevice();
		final Iterator ranges = richLabel.getRanges();
		while (ranges.hasNext()) {
			final StyledString.StyleRange curr = (StyledString.StyleRange) ranges
					.next();
			final StyledString.Style style = curr.style;
			if (style != null) {
				final String foregroundColorName = style
						.getForegroundColorName();
				final String backgroundColorName = style
						.getBackgroundColorName();
				if ((foregroundColorName != null)
						|| (backgroundColorName != null)||style.isStrikeout()||style.isUnderline()) {
					final Color foreground = !isSelected
							&& (foregroundColorName != null) ? this.getColor(
							foregroundColorName, display) : null;
					final Color background = !isSelected
							&& (backgroundColorName != null) ? this.getColor(
							backgroundColorName, display) : null;

					final TextStyle textStyle = new TextStyle(null, foreground,
							background);
					textStyle.underline = style.isUnderline();
					textStyle.strikeout = style.isStrikeout();
					
					this.fTextLayout.setStyle(textStyle, curr.offset,
							curr.offset + curr.length - 1);
				}
			}
		}
		final Rectangle bounds = this.fTextLayout.getBounds();
		final int x = textBounds.x;
		final int y = textBounds.y
				+ Math.max(0, (textBounds.height - bounds.height) / 2);
		this.fTextLayout.draw(gc, x, y);
		this.fTextLayout.setText(""); // clear all ranges //$NON-NLS-1$
	}

	public void dispose() {
		if (this.fTextLayout != null) {
			this.fTextLayout.dispose();
			this.fTextLayout = null;
		}
		if (!this.fControl.isDisposed()) {
			this.fControl.removeListener(SWT.PaintItem, this);
			this.fControl.removeListener(SWT.EraseItem, this);
			this.fControl.removeListener(SWT.Dispose, this);
			this.fControl.removeListener(SWT.MeasureItem, this);
		}
	}
}