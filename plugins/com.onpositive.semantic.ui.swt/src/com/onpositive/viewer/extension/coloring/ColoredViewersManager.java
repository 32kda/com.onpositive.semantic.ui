/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Juerg Billeter, juergbi@ethz.ch - 47136 Search view should show match objects
 *     Ulrich Etter, etteru@ethz.ch - 47136 Search view should show match objects
 *     Roman Fuchs, fuchsro@ethz.ch - 47136 Search view should show match objects
 *     
 * Copied from JDT UI: org.eclipse.jdt.internal.ui.viewsupport.ColoredViewersManager.
 * Will be removed again when made API. https://bugs.eclipse.org/bugs/show_bug.cgi?id=196128
 *******************************************************************************/
package com.onpositive.viewer.extension.coloring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.onpositive.semantic.model.api.roles.IRichLabelProvider;
import com.onpositive.semantic.model.api.roles.StyledString;
import com.onpositive.semantic.model.api.roles.StyledString.Style;
import com.onpositive.semantic.model.ui.property.editors.structured.UniversalLabelProvider;

@SuppressWarnings("unchecked")
public class ColoredViewersManager implements IPropertyChangeListener {

	public static final String COLORED_LABEL_KEY = "coloredlabel"; //$NON-NLS-1$

	public static void clearLabels(ColumnViewer viewer) {
		if (viewer instanceof TableViewer) {
			final TableViewer ts = (TableViewer) viewer;
			for (final TableItem i : ts.getTable().getItems()) {
				i.setData(COLORED_LABEL_KEY, null);
			}
		} else if (viewer instanceof TreeViewer) {
			final TreeViewer ts = (TreeViewer) viewer;
			final TreeItem[] items = ts.getTree().getItems();
			clear(items);
		} else {
			throw new RuntimeException();
		}
	}

	private static void clear(TreeItem[] items) {
		for (final TreeItem i : items) {
			i.setData(COLORED_LABEL_KEY, null);
			clear(i.getItems());
		}
	}

	// temporarily reusing the JDT constants
	private static final String QUALIFIER_FG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.qualifier"; //$NON-NLS-1$

	private static final String DECORATIONS_FG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.decorations"; //$NON-NLS-1$

	private static final String COUNTER_FG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.counter"; //$NON-NLS-1$

	private static final String HIGHLIGHT_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.match_highlight"; //$NON-NLS-1$

	public static final Style QUALIFIER_STYLE = new Style(
			QUALIFIER_FG_COLOR_NAME, null);
	public static final Style COUNTER_STYLE = new Style(COUNTER_FG_COLOR_NAME,
			null);
	public static final Style DECORATIONS_STYLE = new Style(
			DECORATIONS_FG_COLOR_NAME, null);
	public static final Style HIGHLIGHT_STYLE = new Style(null,
			HIGHLIGHT_BG_COLOR_NAME);

	private static ColoredViewersManager fgInstance = new ColoredViewersManager();

	private final Map fManagedViewers;
	private final ColorRegistry fColorRegisty;

	private ColoredViewersManager() {
		this.fManagedViewers = new HashMap();
		this.fColorRegisty = JFaceResources.getColorRegistry();
	}

	public void installColoredLabels(StructuredViewer viewer) {
		if (this.fManagedViewers.containsKey(viewer)) {
			return; // already installed
		}
		if (this.fManagedViewers.isEmpty()) {
			// first viewer installed
			this.fColorRegisty.addListener(this);
		}
		this.fManagedViewers.put(viewer, new ManagedViewer(viewer));
	}

	public void uninstallColoredLabels(StructuredViewer viewer) {
		final ManagedViewer mv = (ManagedViewer) this.fManagedViewers.remove(viewer);
		if (mv == null) {
			return; // not installed
		}

		if (this.fManagedViewers.isEmpty()) {
			this.fColorRegisty.removeListener(this);
			// last viewer uninstalled
		}
	}

	public Color getColorForName(String symbolicName) {
		return this.fColorRegisty.get(symbolicName);
	}

	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();
		if (this.shouldUpdate(property)) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ColoredViewersManager.this.refreshAllViewers();
				}
			});
		}
	}

	private boolean shouldUpdate(String property) {
		return property.equals(QUALIFIER_FG_COLOR_NAME)
				|| property.equals(COUNTER_FG_COLOR_NAME)
				|| property.equals(DECORATIONS_FG_COLOR_NAME)
				|| property.equals(HIGHLIGHT_BG_COLOR_NAME);
	}

	protected final void refreshAllViewers() {
		for (final Iterator iterator = this.fManagedViewers.values().iterator(); iterator
				.hasNext();) {
			final ManagedViewer viewer = (ManagedViewer) iterator.next();
			viewer.refresh();
		}
	}

	private class ManagedViewer implements DisposeListener {

		private final StructuredViewer fViewer;
		private OwnerDrawSupport fOwnerDrawSupport;

		private ManagedViewer(StructuredViewer viewer) {
			this.fViewer = viewer;
			this.fOwnerDrawSupport = null;
			this.fViewer.getControl().addDisposeListener(this);
			if (showColoredLabels()) {
				this.installOwnerDraw();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse
		 * .swt.events.DisposeEvent)
		 */
		public void widgetDisposed(DisposeEvent e) {
			ColoredViewersManager.this.uninstallColoredLabels(this.fViewer);
		}

		public final void refresh() {
			final Control control = this.fViewer.getControl();
			if (!control.isDisposed()) {
				if (showColoredLabels()) {
					this.installOwnerDraw();
				} else {
					this.uninstallOwnerDraw();
				}
			}
		}

		protected void installOwnerDraw() {
			if (this.fOwnerDrawSupport == null) {
				// not yet installed
				this.fOwnerDrawSupport = new OwnerDrawSupport(this.fViewer.getControl(),
						(ColumnViewer) this.fViewer) { // will
					// install
					// itself
					// as
					// listeners
					public StyledString getColoredLabel(Item item, int index) {
						return ManagedViewer.this.getColoredLabelForView(item, index);
					}

					public Color getColor(String foregroundColorName,
							Display display) {
						return ColoredViewersManager.this.getColorForName(foregroundColorName);
					}

					protected void refreshItem(Item item) {

						IBaseLabelProvider labelProvider2 = ManagedViewer.this.fViewer
								.getLabelProvider();
						if (labelProvider2 instanceof UniversalLabelProvider<?>){
							final UniversalLabelProvider<?> labelProvider = (UniversalLabelProvider<?>) labelProvider2;
							final Object data = item.getData();
							item.setData(OwnerDrawSupport.NEEDS_REFRESH, null);
							final Image image = labelProvider.getActualImage(data);
							item.setImage(image);
						}
						else{
							ILabelProvider p=(ILabelProvider) labelProvider2;
							final Object data = item.getData();
							item.setData(OwnerDrawSupport.NEEDS_REFRESH, null);
							final Image image = p.getImage(data);
							item.setImage(image);
						}
					}

					
					public IItemPaintParticipant getPaintParticipantLabel(
							Item item, int index) {
						return ManagedViewer.this.getPainterForView(item, index);
					}
				};
			}
			this.refreshViewer();
		}

		protected IItemPaintParticipant getPainterForView(Item item, int index) {
			final IBaseLabelProvider labelProvider = this.fViewer.getLabelProvider();
			if (labelProvider instanceof IOwnerDrawTableProvider) {
				final IItemPaintParticipant participant = ((IOwnerDrawTableProvider) labelProvider)
						.getParticipant(item.getData(), index);
				if (participant != null) {
					return participant;
				}
			}
			if ((index == 0) && (labelProvider instanceof IOwnerDrawLabelProvider)) {
				return ((IOwnerDrawLabelProvider) labelProvider)
						.getParticipant(item.getData());
			}
			return null;
		}

		protected void uninstallOwnerDraw() {
			if (this.fOwnerDrawSupport == null) {
				return; // not installed
			}

			this.fOwnerDrawSupport.dispose(); // removes itself as listener
			this.fOwnerDrawSupport = null;
			this.refreshViewer();
		}

		private void refreshViewer() {
			final Control control = this.fViewer.getControl();
			if (!control.isDisposed()) {
				if (control instanceof Tree) {
					this.refresh(((Tree) control).getItems());
				} else if (control instanceof Table) {
					this.refresh(((Table) control).getItems());
				}
			}
		}

		private void refresh(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				final Item item = items[i];
				item.setData(COLORED_LABEL_KEY, null);
				item.setData(OwnerDrawSupport.NEEDS_REFRESH, null);
				final String text = item.getText();
				item.setText(""); //$NON-NLS-1$
				item.setText(text);
				if (item instanceof TreeItem) {
					this.refresh(((TreeItem) item).getItems());
				}
			}
		}

		private StyledString getColoredLabelForView(Item item, int index) {
			final IBaseLabelProvider labelProvider = this.fViewer.getLabelProvider();
			StyledString newLabel = null;
			if (labelProvider instanceof ITableRichLabelProvider) {
				newLabel = ((ITableRichLabelProvider) labelProvider)
						.getRichTextLabel(item.getData(), index);
				return newLabel;
			}
			final String itemText = item.getText();
//			final StyledString oldLabel = (StyledString) item
//					.getData(COLORED_LABEL_KEY);
//			if ((oldLabel != null) && oldLabel.getString().equals(itemText)) {
//				// avoid accesses to the label provider if possible
//				return oldLabel;
//			}
			if (labelProvider instanceof IRichLabelProvider) {
				newLabel = ((IRichLabelProvider) labelProvider)
						.getRichTextLabel(item.getData());
			}
			if (newLabel == null) {
				newLabel = new StyledString(itemText); // fallback. Should
				// never happen.
			}
			item.setData(COLORED_LABEL_KEY, newLabel); // cache the result
			return newLabel;
		}

	}

	public static boolean showColoredLabels() {
		return true;
	}

	public static void install(StructuredViewer viewer) {
		fgInstance.installColoredLabels(viewer);
	}

	public static StyledString decorateColoredString(StyledString string,
			String decorated, Style color) {
		final String label = string.getString();
		final int originalStart = decorated.indexOf(label);
		if (originalStart == -1) {
			return new StyledString(decorated); // the decorator did
			// something wild
		}
		if (originalStart > 0) {
			final StyledString newString = new StyledString(decorated.substring(0,
					originalStart), color);
			newString.append(string);
			string = newString;
		}
		if (decorated.length() > originalStart + label.length()) { // decorator
			// appended
			// something
			return string.append(decorated.substring(originalStart
					+ label.length()), color);
		}
		return string; // no change
	}
}
