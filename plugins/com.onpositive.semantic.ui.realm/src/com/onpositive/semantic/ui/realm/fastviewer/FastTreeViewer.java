package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FastTreeViewer {

	HashMap columnToControl = new HashMap();

	private final class ChildResolver extends Thread {
		private final RowItem row;
		private Runnable toRun;

		private ChildResolver(RowItem row, Runnable toRun) {
			this.row = row;
			this.toRun = toRun;
		}

		public void run() {
			try {
				provider.addThread(this);

				final List children = row.getChildren();

				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						if (!provider.hasThead(ChildResolver.this)) {
							return;
						}
						if (tv.getTable().isDisposed()) {
							return;
						}
						boolean hasV = tv.getTable().getVerticalBar()
								.isVisible();
						RowItem z = row;
						while (z != null) {
							if (!z.isExpanded()) {
								return;
							}
							z = z.getParentItem();
						}
						try {
							tv.getControl().setRedraw(false);
							repaint = false;
							provider.collapseChildren2(row);
							provider.insertChildren(row, children);
						} finally {
							tv.getControl().setRedraw(true);
							tv.getControl().update();
							repaint = true;
						}
						if (toRun != null) {
							toRun.run();
						} else if (children.size() == 1) {
							triggerExpandCollapse(provider
									.getRow(row.index + 1));
						}
						boolean visible = tv.getTable().getHorizontalBar()
								.isVisible();
						if (visible) {
							tv.getTable().getParent().layout(true, true);
						} else if (!hasV) {
							boolean hasV1 = tv.getTable().getVerticalBar()
									.isVisible();
							if (hasV1) {
								tv.getTable().getParent().layout(true, true);
							}
						}
					}

				});
			} finally {
				provider.removeThread(this);
			}
		}
	}

	private static final int TREE_ITEM_SHIFT = 10;

	private static final int LEVEL_SHIFT = 20;

	private static final int EXPAND_SHIFT = 10;

	private static final int EXTRA = 20;

	TableViewer tv;

	private Color selectionColor = Display.getCurrent().getSystemColor(
			SWT.COLOR_WIDGET_LIGHT_SHADOW);

	private Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

	private Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

	LazyTreeContentProvider provider;

	private Slider slider;
	private ArrayList columnds = new ArrayList();

	int max;
	private int width;

	private int fontSize;

	private SavablePart savablePart;

	public void addColumn(final FastTreeColumn column) {
		columnds.add(column);
		final TableColumn tableColumn = new TableColumn(tv.getTable(), SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setResizable(column.isResizable());
		TableColumnLayout ll = (TableColumnLayout) tv.getTable().getParent()
				.getLayout();
		tableColumn.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				if (!columnds.isEmpty()) {

					if (tv.getTable().getColumn(0) == tableColumn) {
						width = tableColumn.getWidth();
						if (slider != null) {
							slider.setThumb(width);
							slider.setSize(width, 16);

							slider.redraw();
						}
					}
					tv.getControl().redraw();
				}
			}
		});
		TableSortController tableSortController = new TableSortController(tv,
				tableColumn, column.getComparator(), true) {

			protected void update() {

				provider.captureSelection();
				provider.setComparator(getComparator());
				if (savablePart != null) {
					savablePart.saveSorting(column.getColumnId(),
							getSortDirection() == SWT.UP);
				}
				if (tv.getTable().getHorizontalBar().isVisible()) {
					tv.getTable().getParent().layout(true);
				}
			}

		};
		Image image = column.getImage();
		if (image != null) {
			tableColumn.setImage(image);
		}
		columnToControl.put(column, tableSortController);
		tableColumn.setData("sorter", tableSortController);
		ColumnLayoutData layoutData = column.getLayoutData(fontSize);
		ll.setColumnData(tableColumn, layoutData);

		tableColumn.setText(column.getTitle());
		tableColumn.setToolTipText(column.getTooltip());
	}

	private boolean repaint;
	private int generation;

	public void setSavablePart(SavablePart part) {
		this.savablePart = part;
	}

	public void create(Composite parent) {
		final Table table = new Table(parent, SWT.VIRTUAL | SWT.FULL_SELECTION
				| SWT.NONE | SWT.MULTI);
		table.setToolTipText(null);

		tv = new TableViewer(table);

		tv.getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {

			}

			public void keyReleased(KeyEvent e) {
				if (e.character == '+' || e.keyCode == SWT.ARROW_RIGHT) {
					TableItem[] selection = table.getSelection();
					if (selection.length == 1) {
						RowItem item = (RowItem) selection[0].getData();
						if (item.isExpandable() && !item.isExpanded()) {
							triggerExpandCollapse(item);
						}
					}
				}
				if (e.character == '-' || e.keyCode == SWT.ARROW_LEFT) {
					TableItem[] selection = table.getSelection();
					if (selection.length == 1) {
						RowItem item = (RowItem) selection[0].getData();
						if (item.isExpanded()) {
							triggerExpandCollapse(item);
						}
					}
				}
			}

		});

		tv.setLabelProvider(new LabelProvider() {

			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return "";
			}

		});

		table.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				if (!ignoreEvent) {
					if (!columnds.isEmpty()) {
						TableColumn column = table.getColumn(0);
						width = column.getWidth();
						if (slider != null) {
							slider.setThumb(width);
							slider.setSize(width, 16);
							slider.redraw();
						}
					}
					table.getParent().layout(true, true);
				}
			}

		});
		GC gc = new GC(table);
		fontSize = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();
		table.addListener(SWT.Paint, new Listener() {

			public void handleEvent(Event event) {
				if (repaint) {
					// repaint = false;
					return;
				}

				RowItem[] visibleItems = provider.getVisibleItems();
				max = 0;
				for (int a = 0; a < visibleItems.length; a++) {
					max = Math.max(max, measure(visibleItems[a]));
				}
				if (slider != null) {
					slider.setMaximum(max);
					slider.setThumb(width);
					if (max <= width && sliderOffset > 0) {
						sliderOffset = 0;
						slider.setSelection(0);
					}
				}
				repaint = true;
				table.getDisplay().asyncExec(new Runnable() {

					public void run() {
						try {
							repaint = true;
							generation++;
							if (!table.isDisposed()) {
								// System.out.println("Force");
								table.redraw();
								table.update();
							}
						} finally {
							repaint = false;
						}
					}

				});

			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		provider = new LazyTreeContentProvider(this);
		tv.setContentProvider(provider);
		table.addListener(SWT.MeasureItem, new Listener() {

			public void handleEvent(Event event) {
				// if (event.index == 0) {
				// event.width = width - 3;
				// } else {
				event.width = tv.getTable().getColumn(event.index).getWidth() - 10;
				// }
			}
		});
		table.getVerticalBar().addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				int topIndex = Math.max(tv.getTable().getTopIndex() - 4, 0);
				int i = Math.min(topIndex + tv.getTable().getSize().y
						/ tv.getTable().getItemHeight() + 40, tv.getTable()
						.getItemCount());
				tv.getControl().setRedraw(false);
				try {
					for (int a = topIndex; a < i; a++) {
						tv.clear(a);
					}
				} finally {
					tv.getControl().setRedraw(true);
					tv.getControl().redraw();
				}
			}

		});
		table.addListener(SWT.EraseItem, new Listener() {

			public void handleEvent(Event event) {
				// System.out.println("R");
				final TableItem item = (TableItem) event.item;
				// final Rectangle bounds = item.getBounds(event.index);
				if ((event.detail & SWT.SELECTED) != 0) {
					drawSelection(event);
				} else {
					// System.out.println(event.width);
					// final Rectangle clipping = event.gc.getClipping();
					event.gc.setBackground(event.gc.getDevice().getSystemColor(
							SWT.COLOR_WHITE));
					event.gc.fillRectangle(event.x, event.y, event.width,
							event.height);
					// }
				}
				event.detail &= ~SWT.SELECTED;
				event.detail &= ~(1 << 5);
				// event.detail &= ~SWT.HOT;
				event.detail &= ~SWT.FOREGROUND;
			}
		});
		table.addListener(SWT.PaintItem, new Listener() {

			public void handleEvent(Event event) {
				// long l0=System.currentTimeMillis();
				RowItem row = (RowItem) event.item.getData();
				int level = event.index == 0 ? row.getLevel() : event.index;

				if (event.index == 0) {

					event.x -= sliderOffset;
					if (isTree) {
						event.x += level * LEVEL_SHIFT;
						if (event.index == 0) {
							if (row.isExpandable()) {
								drawExpandSign(row.isExpanded(), event, row);
							}
						}
						event.x += (TREE_ITEM_SHIFT + EXPAND_SHIFT);
					}
				}

				if (columnds.size() > event.index && event.index >= 0) {
					FastTreeColumn fastTreeColumn = (FastTreeColumn) columnds
							.get(event.index);
					IColumnRenderer renderer = fastTreeColumn.getRenderer();
					if (renderer != null) {
						renderer.renderColumn(row, event);
					} else {
						event.gc.drawText("No Renderer", event.x, event.y);
					}
				}
				// long l1=System.currentTimeMillis();
				// System.out.println(l1-l0);
			}
		});
		table.addListener(SWT.MouseUp, new Listener() {

			public void handleEvent(Event event) {
				if (isTree) {
					int x = event.x + sliderOffset;
					TableItem item = table.getItem(new Point(x, event.y));
					if (item != null) {
						RowItem row = (RowItem) item.getData();
						if (row != null) {
							int min = 0;
							min += row.getLevel() * LEVEL_SHIFT
									+ TREE_ITEM_SHIFT / 2;
							int max = min + EXPAND_SHIFT + TREE_ITEM_SHIFT;
							if (x > min && x < max) {
								triggerExpandCollapse(row);
							}
						}
					}
				}
			}
		});
	}

	protected int measure(RowItem rowItem) {
		int x = 0;
		x += rowItem.getLevel() * LEVEL_SHIFT + TREE_ITEM_SHIFT + EXPAND_SHIFT
				+ EXTRA;
		if (columnds.size() > 0) {
			FastTreeColumn fastTreeColumn = (FastTreeColumn) columnds.get(0);
			IColumnRenderer renderer = fastTreeColumn.getRenderer();
			if (renderer != null) {
				return x + renderer.measureWidth(rowItem);
			}
		}
		return x;
	}

	protected void triggerExpandCollapse(final RowItem row) {
		if (row.isExpandable()) {
			if (!row.isExpanded()) {
				expandNode(row, null);
			} else {
				ITreeNode node = row.getNode();
				if (node != null) {
					provider.collapseChildren(row);
					boolean visible = tv.getTable().getHorizontalBar()
							.isVisible();
					if (visible) {
						tv.getTable().getParent().layout(true, true);
					}
				}
			}
		}
	}

	void expandNode(final RowItem row, Runnable toRun) {
		ITreeNode node = row.getNode();
		if (node != null) {
			boolean hasV = tv.getTable().getVerticalBar().isVisible();
			ITreeNode node0 = new ITreeNode() {

				public List getChildren() {
					return null;
				}

				public boolean hasChildren() {
					return false;
				}

				public String toString() {
					return "Resolving children";
				}

				
				public Object getNodeObject() {
					return null;
				}

			};
			repaint = false;
			tv.getControl().setRedraw(false);
			provider.insertChildren(row, new ArrayList(Collections
					.singletonList(node0)));
			tv.getControl().setRedraw(true);
			tv.getControl().update();
			repaint = true;
			Thread sm = new ChildResolver(row, toRun);
			sm.start();
			boolean visible = tv.getTable().getHorizontalBar().isVisible();
			if (visible) {
				tv.getTable().getParent().layout(true, true);
			} else if (!hasV) {
				boolean hasV1 = tv.getTable().getVerticalBar().isVisible();
				if (hasV1) {
					tv.getTable().getParent().layout(true, true);
				}

			}
		}
	}

	protected void drawExpandSign(boolean expanded, Event event, RowItem row) {
		int oldY = event.y;
		int y = oldY + 5;
		int size = TREE_ITEM_SHIFT + event.x;
		if (isHot(row)) {
			// event.gc.setBackground(ProfilerUISettings.getInstance()
			// .getHotElement());
		} else {
			event.gc.setBackground(black);
		}
		if (!expanded) {
			{
				event.gc.fillPolygon(new int[] { size, y, size + 5, y + 5,
						size, y + 10 });
			}
		} else {
			event.gc.fillPolygon(new int[] { size, y + 8, size + 7, y,
					size + 7, y + 8 });
		}
		event.gc.setBackground(white);
	}

	private boolean isHot(RowItem row) {
		RowItem parentItem = row.getParentItem();

		return false;
	}

	protected void drawSelection(Event event) {
		event.gc.setBackground(selectionColor);
		event.gc.fillRectangle(event.x, event.y, event.width, event.height);
	}

	public void setInput(ITreeNode infiniteTreeNode) {
		if (infiniteTreeNode != null) {
			tv.setInput(infiniteTreeNode);
		}
	}

	private int sliderOffset;

	private boolean isTree;

	private Runnable contentCallback;

	public void setSlider(final Slider slider) {
		this.slider = slider;
		if (slider != null) {
			slider.setIncrement(5);
			slider.setPageIncrement(50);
			slider.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {

				}

				public void widgetSelected(SelectionEvent e) {
					sliderOffset = slider.getSelection();
					tv.getTable().redraw();
				}
			});
		}
	}

	public Control getControl() {
		return tv.getControl();
	}

	public void addDoubleClickListener(IDoubleClickListener doubleClickListener) {
		tv.addDoubleClickListener(doubleClickListener);
	}

	public void removeAllColumns() {
		columnds.clear();
		columnToControl.clear();
		TableColumn[] columns = tv.getTable().getColumns();
		for (int a = 0; a < columns.length; a++) {
			columns[a].dispose();
		}
	}

	public void setIsTree(boolean isTree) {
		this.isTree = isTree;
		provider.cleanupExpanded(this.isTree);

	}

	public void addOpenListener(IOpenListener openListener) {
		tv.addOpenListener(openListener);
	}

	public ISelection getSelection() {
		return tv.getSelection();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		tv.addSelectionChangedListener(listener);
	}

	Runnable extras = new Runnable() {

		public void run() {
			if (tv.getTable().getItemCount() > 0) {
				tv.setSelection(new StructuredSelection(tv.getTable()
						.getItem(0).getData()));
			} else {
				tv.setSelection(new StructuredSelection());
			}

		}

	};

	public DrillFrame goForward(ITreeNode sl) {
		DrillFrame goForward = provider.goForward(sl);
		return goForward;
	}

	public void restore(DrillFrame frame) {
		provider.restore(frame);
	}

	public StructuredViewer getStructuredViewer() {
		return tv;
	}

	public List getColumns() {
		return columnds;
	}

	boolean ignoreEvent = false;

	public void removeColumn(FastTreeColumn column) {
		try {
			ignoreEvent = true;
			for (int a = 0; a < columnds.size(); a++) {
				FastTreeColumn cm = (FastTreeColumn) columnds.get(a);
				if (cm.getColumnId().equals(column.getColumnId())) {
					tv.getTable().getColumn(a).dispose();
					columnds.remove(a);
					return;
				}
			}
		} finally {
			ignoreEvent = false;
		}
	}

	public void expand() {
		// RowItem row = this.provider.getRow(0);
		// triggerExpandCollapse(row);
	}

	public void collapseAll() {
		this.provider.collapseAll();
	}

	public void setSortColumn(FastTreeColumn column, boolean sortDirection) {
		// this.provider.setComparator(column.getComparator());
		TableSortController object = (TableSortController) columnToControl
				.get(column);
		if (object != null) {

			if (sortDirection) {
				object.setState(sortDirection);
				object.setState(!sortDirection);
			} else {
				object.setState(!sortDirection);
			}
		}
	}

	public void notifyContentInited() {
		if (extras != null) {
			extras.run();
		}
		if (contentCallback != null) {
			contentCallback.run();
		}
	}

	public void setContentCallback(Runnable runnable) {
		this.contentCallback = runnable;
	}

}
