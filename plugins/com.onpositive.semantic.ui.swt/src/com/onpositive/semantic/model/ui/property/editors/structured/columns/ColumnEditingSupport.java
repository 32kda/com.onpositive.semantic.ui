/**
 * 
 */
package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyLookup;
import com.onpositive.semantic.model.api.property.PropertyProviderLookup;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.RealmLazyTreeContentProvider;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.CellEditorRegistry;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.EmptyCellEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.ICellEditorFactory;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.IRichCellEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.IRichCellEditorSupport;
import com.onpositive.semantic.model.ui.viewer.structured.RealmLazyContentProvider;
import com.onpositive.viewer.extension.coloring.OwnerDrawSupport;

public class ColumnEditingSupport extends EditingSupport implements
		IRichCellEditorSupport {

	/**
		 * 
		 */
	private final Column column;

	private Object element;

	protected int columnIndex;

	private int rowIndex;

	private CellEditor cellEditor;
	
	CellEditor internalGetEditor(Object element) {

		if (this.getOwnerSelector() != null
				&& this.getOwnerSelector().isAsTree()) {
			if (element instanceof ITreeNode<?>) {
				final ITreeNode<?> node = (ITreeNode<?>) element;
				final Object item = node.getElement();
				if (item instanceof IClusterizationPoint<?>) {
					final IClusterizationPoint<?> point = (IClusterizationPoint<?>) item;
					Object primaryValue = point.getPrimaryValue();
					if (primaryValue == null) {
						final IPropertyLookup propertyProvider = point
								.getAdapter(IPropertyLookup.class);
						if (propertyProvider != null) {
							final IProperty prop = (IProperty) propertyProvider
									.getProperty(item, column.getId());
							if (prop != null) {
								final ICellEditorFactory factory = (ICellEditorFactory) column.getCellEditorFactory();
								if (factory != null) {
									return factory.createEditor(element, item,
											getViewer(), prop);
								}
								return CellEditorRegistry.createEditor(element,
										item, getViewer(), prop, column
												.getRole(), column
												.getTheme());
							}
						}
						return null;
					} else {
						element = primaryValue;
					}
				} else {
					element = item;
				}
			}
		}

		if (column.getProperty() != null) {
			final Object item = column.getElement(element);
			final ICellEditorFactory factory = (ICellEditorFactory) column
					.getCellEditorFactory();
			if (factory != null) {
				return factory.createEditor(element, item, getViewer(), column.getProperty());
			}
			return CellEditorRegistry.createEditor(element, item, 
					getViewer(), column.getProperty(),
					column.getRole(), column.getTheme());
		} else {
			final IPropertyLookup propertyProvider = PropertyProviderLookup
					.getPropertyProvider(element);

			if (propertyProvider != null) {
				final Object item = column.getElement(element);
				final IProperty prop = (IProperty) propertyProvider
						.getProperty(element, column.getId());
				if (prop != null) {
					return CellEditorRegistry.createEditor(element, item,
							this.getViewer(), prop,
							column.getRole(), column.getTheme());
				}
			}
		}
		return null;
	}

	private IListElement<?> getOwnerSelector() {
		return column.getOwnerSelector();
	}

	public ColumnEditingSupport(Column column, final ColumnViewer viewer) {
		super(viewer);
		this.column = column;
		viewer.getControl().addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {

			}

			public void mouseDown(MouseEvent e) {
				Object data = null;
				final Point point = new Point(e.x, e.y);
				if (viewer instanceof TableViewer) {
					final TableViewer vr = (TableViewer) viewer;

					final TableItem item = vr.getTable().getItem(point);
					if (item != null) {
						if (item.getBounds(
								ColumnEditingSupport.this.columnIndex)
								.contains(point)) {
							data = item.getData();
						}
					}
				} else if (viewer instanceof TreeViewer) {
					final TreeViewer vr = (TreeViewer) viewer;
					final TreeItem item = vr.getTree().getItem(point);
					if (item != null) {
						if (item.getBounds(
								ColumnEditingSupport.this.columnIndex)
								.contains(point)) {
							data = item.getData();
						}
					}
				}
				if (data != null) {
					if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
						final IRichCellEditor ce = (IRichCellEditor) ColumnEditingSupport.this.cellEditor;
						ce.mouseDownOnElement(data);
					}
				}
			}

			public void mouseUp(MouseEvent e) {

			}

		});
	}

	protected boolean canEdit(Object element) {
		return column.isEditable();
	}

	

	protected void nextCell(boolean increase) {
		final Control control = getViewerControl();
		if (this.column.getOwnerSelector() != null
				&& this.column.getOwnerSelector().isAsTree()) {
			final int index = this.columnIndex;
			final Tree ta = (Tree) control;
			final int[] columnOrder = ta.getColumnOrder();
			int actualIndex = -1;
			for (int a = 0; a < columnOrder.length; a++) {
				if (columnOrder[a] == index) {
					actualIndex = a;
					break;
				}
			}
			if (increase) {
				actualIndex++;
			} else {
				actualIndex--;
			}
			if (actualIndex < 0) {
				actualIndex = this.column.getOwnerSelector().getColumnCount() - 1;
			} else if (this.column.getOwnerSelector().getColumnCount() <= actualIndex) {
				actualIndex = 0;
			}
			final TreeViewer tableViewer = ((TreeViewer) getViewer());
			tableViewer.editElement(this.element, columnOrder[actualIndex]);
		} else {
			final int index = this.columnIndex;
			if (control instanceof Tree) {
				final Tree ta = (Tree) control;
				final int[] columnOrder = ta.getColumnOrder();
				int actualIndex = -1;
				for (int a = 0; a < columnOrder.length; a++) {
					if (columnOrder[a] == index) {
						actualIndex = a;
						break;
					}
				}
				if (increase) {
					actualIndex++;
				} else {
					actualIndex--;
				}
				final TreeViewer tableViewer = (TreeViewer) (getViewer());
				int length = (tableViewer).getTree().getColumns().length;
				final int ti = length;
				if (ti <= actualIndex) {
					actualIndex = 0;
				} else if (actualIndex < 0) {
					actualIndex = length - 1;
				}

				tableViewer.editElement(this.element, columnOrder[actualIndex]);
			} else {
				final Table ta = (Table) control;
				final int[] columnOrder = ta.getColumnOrder();
				int actualIndex = -1;
				for (int a = 0; a < columnOrder.length; a++) {
					if (columnOrder[a] == index) {
						actualIndex = a;
						break;
					}
				}
				if (increase) {
					actualIndex++;
				} else {
					actualIndex--;
				}
				final int ti = this.column.getOwnerSelector().getColumnCount();
				if (ti <= actualIndex) {
					actualIndex = 0;
				} else if (actualIndex < 0) {
					actualIndex = this.column.getOwnerSelector().getColumnCount() - 1;
				}
				final TableViewer tableViewer = ((TableViewer) getViewer());
				tableViewer.editElement(this.element, columnOrder[actualIndex]);
			}
		}
	}

	public Rectangle getClientArea() {
		final Control control = getViewerControl();
		if (control instanceof Table) {
			return ((Table) control).getClientArea();
		} else {
			return ((Tree) control).getClientArea();
		}
	}

	public int getItemHeight() {
		final Control control = getViewerControl();
		if (control instanceof Table) {
			return ((Table) control).getItemHeight();
		} else {
			return ((Tree) control).getItemHeight();
		}
	}

	public int getElementCount() {
		final Control control = getViewerControl();
		if (control instanceof Table) {
			return ((Table) control).getItemCount();
		} else {
			return ((Tree) control).getItemCount();
		}
	}

	protected Control getViewerControl() {
		return getViewer()
				.getControl();
	}

	public int getTopIndex() {
		final Control control =getViewerControl();
		if (control instanceof Table) {
			return ((Table) control).getTopIndex();
		} else {

			Tree tree = (Tree) control;
			TreeItem[] items = tree.getItems();
			return Arrays.asList(items).indexOf(tree.getTopItem());
			// TreeItem topItem = ((Tree)control).getTopItem();
			// return topItem;
		}
	}

	public Object getElementAt(int index) {
		final Control control = this.getViewer().getControl();
		if (control instanceof Table) {
			Object elementAt = ((TableViewer) this.getViewer())
					.getElementAt(index);
			if (elementAt == null) {
				((RealmLazyContentProvider) this.getViewer()
						.getContentProvider()).updateElement(index);
				elementAt = ((TableViewer) this.getViewer())
						.getElementAt(index);
			}
			return elementAt;
		} else {
			Object input = this.getViewer().getInput();
			if (input instanceof ITreeNode<?>) {
				final ITreeNode<?> node = (ITreeNode<?>) input;
				return this.treeContentProvider().getChild(node, index);
			} else {
				Object elementAt = ((TreeViewer) this.getViewer()).getTree()
						.getItems()[index].getData();
				if (elementAt == null) {
					((RealmLazyContentProvider) this.getViewer()
							.getContentProvider()).updateElement(index);
					elementAt = ((TableViewer) this.getViewer())
							.getElementAt(index);
				}
				return elementAt;
			}
			// return ((TreeViewer)getViewer()).getElementAt(index);
		}
	}

	private RealmLazyTreeContentProvider treeContentProvider() {
		return ((RealmLazyTreeContentProvider) this.getViewer()
				.getContentProvider());
	}

	public int getVisibleItemCount() {
		final int start = this.getTopIndex();
		final int itemCount = this.getElementCount();
		final ColumnViewer viewer2 = this.getViewer();
		if (viewer2 instanceof TableViewer) {
			final Table table = ((TableViewer) viewer2).getTable();
			return Math.min(table.getBounds().height / table.getItemHeight()
					+ 2, itemCount - start);
		}
		if (viewer2 instanceof TreeViewer) {
			final Tree table = ((TreeViewer) viewer2).getTree();
			return Math.min(table.getBounds().height / table.getItemHeight()
					+ 2, itemCount - start);
		}
		return -1;
	}

	public int getHeight() {
		final ColumnViewer viewer2 = this.getViewer();
		if (viewer2 instanceof TableViewer) {
			final Table table = ((TableViewer) viewer2).getTable();
			return table.getItemHeight();
		}
		if (viewer2 instanceof TreeViewer) {
			final Tree table = ((TreeViewer) viewer2).getTree();
			table.getItemHeight();
		}
		return -1;
	}

	protected void movePageDown() {
		final ColumnViewer tableViewer = (this.getViewer());

		if (tableViewer instanceof TreeViewer) {
			final Tree table = ((TreeViewer) tableViewer).getTree();
			final int visibleItemCount = table.getBounds().height
					/ (table.getItemHeight() + 2);
			if (this.element instanceof ITreeNode) {
				final ITreeNode<?> treeItem2 = (ITreeNode<?>) this.element;
				ITreeNode<?> result = treeItem2;
				for (int a = 0; a < visibleItemCount; a++) {
					final ITreeNode<?> pr = this.treeContentProvider().getNext(
							result);
					if (pr != null) {
						result = pr;
					} else {
						break;
					}
				}
				if (result != null) {
					this.doEdit(tableViewer, result);
				}
			}
			else{
				int index = this.getTopIndex();
				final int page = Math.max(1, this.getVisibleItemCount());
				final int end = this.getElementCount() - 1;
				index = Math.min(end, index + page - 1);
				final Object elementAt = this.getElementAt(index);
				tableViewer.editElement(elementAt, this.columnIndex);	
			}
			return;
		} else {
			int index = this.getTopIndex();
			final int page = Math.max(1, this.getVisibleItemCount());
			final int end = this.getElementCount() - 1;
			index = Math.min(end, index + page - 1);
			final Object elementAt = this.getElementAt(index);
			tableViewer.editElement(elementAt, this.columnIndex);
		}
	}

	protected void movePageUp() {
		final ColumnViewer tableViewer = this.getViewer();
		if (tableViewer instanceof TreeViewer) {
			final Tree table = ((TreeViewer) tableViewer).getTree();
			final int visibleItemCount = table.getBounds().height
					/ (table.getItemHeight() + 2);
			if (element instanceof ITreeNode) {
				final ITreeNode<?> treeItem2 = (ITreeNode<?>) this.element;
				ITreeNode<?> result = treeItem2;
				for (int a = 0; a < visibleItemCount; a++) {
					final ITreeNode<?> pr = this.treeContentProvider().getPrev(
							result);
					if (pr != null) {
						result = pr;
					} else {
						break;
					}
				}
				if (result != null) {
					this.doEdit(tableViewer, result);
				}
				return;
			}
		}
		int index = this.getTopIndex();
		if (index == this.rowIndex) {
			final int page = this.getVisibleItemCount();
			index = Math.max(0, index - page + 1);
		}
		tableViewer.editElement(this.getElementAt(index), this.columnIndex);
	}

	protected void moveUp() {
		final ColumnViewer tableViewer = getViewer();
		final int ro = this.rowIndex;
		if (ro > 0) {
			this.doEdit(tableViewer, this.getElementAt(ro - 1));
		} else {
			if (ro == -1) {
				if (this.element instanceof ITreeNode<?>) {
					final ITreeNode<?> treeItem2 = (ITreeNode<?>) this.element;
					final ITreeNode<?> result = this.treeContentProvider()
							.getPrev(treeItem2);
					if (result != null) {
						this.doEdit(tableViewer, result);
					}
				}
			}
		}
	}

	protected void moveHome() {
		final ColumnViewer tableViewer = getViewer();
		this.doEdit(tableViewer, this.getElementAt(0));
	}
	
	@Override
	public ColumnViewer getViewer() {
		return (ColumnViewer) ((ListEnumeratedValueSelector)column.getOwnerSelector()).getViewer();
	}

	protected void moveEnd() {
		final ColumnViewer tableViewer = getViewer();
		final Object elementAt = this.getElementAt(this.getElementCount() - 1);
		if (tableViewer instanceof TreeViewer) {
			if (elementAt instanceof ITreeNode<?>) {
				ITreeNode<?> node = (ITreeNode<?>) elementAt;
				final RealmLazyTreeContentProvider treeContentProvider = this
						.treeContentProvider();
				ITreeNode<?>[] items = treeContentProvider
						.getChildrenSorted(node);
				while ((items != null) && (items.length > 0)) {
					if (((TreeViewer) tableViewer).getExpandedState(node)) {
						node = items[treeContentProvider.isInverse() ? items.length - 1
								: 0];
						items = treeContentProvider.getChildrenSorted(node);
					} else {
						break;
					}
				}
				this.doEdit(tableViewer, node);
			}
			return;
		}
		this.doEdit(tableViewer, elementAt);
	}

	protected void moveDown() {
		final ColumnViewer tableViewer =getViewer();
		final int ro = this.rowIndex;
		if (this.rowIndex == -1) {
			if (this.element instanceof ITreeNode<?>) {
				final ITreeNode<?> treeItem2 = (ITreeNode<?>) this.element;
				final ITreeNode<?> result = this.treeContentProvider().getNext(
						treeItem2);

				this.doEdit(tableViewer, result);
			}
		} else {
			if (ro < this.getElementCount() - 1) {
				final Object elementAt = this.getElementAt(ro + 1);
				this.doEdit(tableViewer, elementAt);
			}
		}
	}

	protected void doEdit(ColumnViewer tableViewer, Object el) {
		if (el instanceof TreeItem) {
			final TreeItem it = (TreeItem) el;
			el = it.getData();
		}
		if (el != null) {
			tableViewer.editElement(el, this.columnIndex);
		}
	}

	protected void initializeCellEditorValue(CellEditor cellEditor,
			ViewerCell cell) {
		this.initEditor(cellEditor, cell);
		super.initializeCellEditorValue(cellEditor, cell);
	}

	protected void saveCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
		if (cellEditor.isValueValid()) {
			super.saveCellEditorValue(cellEditor, cell);
		}
	}

	protected void initEditor(final CellEditor pcellEditor, ViewerCell cell) {
		this.element = cell.getElement();
		this.columnIndex = cell.getColumnIndex();
		this.cellEditor = pcellEditor;
		pcellEditor.addListener(new ICellEditorListener() {

			public void applyEditorValue() {
				pcellEditor.removeListener(this);
				ColumnEditingSupport.this.cellEditor = null;

			}

			public void cancelEditor() {
				pcellEditor.removeListener(this);
				ColumnEditingSupport.this.cellEditor = null;
			}

			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {

			}

		});
		final Control tl =getViewerControl();
		if (tl instanceof Table) {
			this.rowIndex = ((Table) tl).indexOf((TableItem) cell.getItem());
		} else {
			this.rowIndex = -1;
		}
	}

	protected CellEditor getCellEditor(Object element) {
		final ListEnumeratedValueSelector<Object> ownerSelector = (ListEnumeratedValueSelector<Object>) this.column.getOwnerSelector();
		if (ownerSelector instanceof TableEnumeratedValueSelector) {
			((TableEnumeratedValueSelector) ownerSelector)
					.setEditingColumn(this.column);
		}
		final CellEditor internalGetEditor = column.canActuallyEdit(element) ? internalGetEditor(element) : new EmptyCellEditor(
				(Composite) getViewerControl());
		if (this.cellEditor != null) {
			this.cellEditor.dispose();
			this.cellEditor = null;
		}
		if (internalGetEditor != null) {
			final Control control = internalGetEditor.getControl();
			if (control != null) {
				this.registerListeners(control);
			}
		}
		if (internalGetEditor instanceof IRichCellEditor) {
			final IRichCellEditor cl = (IRichCellEditor) internalGetEditor;
			cl.initCellEditor(element, this);
		}
		this.getViewer().setData(OwnerDrawSupport.CELL_EDITING,
				this.columnIndex);
		this.getViewer().setData(OwnerDrawSupport.CELL_EDITING_DATA, element);
		return internalGetEditor;
	}

	KeyListener keyListener = new KeyListener() {

		boolean sheduled;

		public void keyPressed(KeyEvent e) {

			if (!e.doit) {
				return;
			}
			if (e.keyCode == SWT.ESC) {
				cellEditor.deactivate();
			}
			if (e.keyCode == SWT.PAGE_UP) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesPageUp()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.movePageUp();
							} finally {
								sheduled = false;
							}
						}
					});
					this.sheduled = true;

				}
				e.doit = false;
			}
			if (e.keyCode == SWT.PAGE_DOWN) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesPageDown()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.movePageDown();
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			}
			if (e.keyCode == SWT.END) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesEnd()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.moveEnd();
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			}
			if (e.keyCode == SWT.HOME) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesHome()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.moveHome();
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			}
			if (e.keyCode == SWT.ARROW_UP) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesUp()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.moveUp();
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			} else if (e.keyCode == SWT.ARROW_DOWN) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesDown()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.moveDown();
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;
				}
				e.doit = false;
			} else if (e.keyCode == SWT.ARROW_LEFT) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesLeft()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.nextCell(false);
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				if (ColumnEditingSupport.this.cellEditor instanceof IRichCellEditor) {
					if (((IRichCellEditor) ColumnEditingSupport.this.cellEditor)
							.handlesRight()) {
						return;
					}
				}
				if (!this.sheduled) {
					Display.getCurrent().asyncExec(new Runnable() {

						public void run() {
							try {
								ColumnEditingSupport.this.nextCell(true);
							} finally {
								sheduled = false;
							}
						}

					});
					this.sheduled = true;

				}
				e.doit = false;
			}
		}

		public void keyReleased(KeyEvent e) {

		}

	};

	TraverseListener traverseListener = new TraverseListener() {

		public void keyTraversed(TraverseEvent e) {
			if (e.keyCode == SWT.TAB) {
				ColumnEditingSupport.this.nextCell(true);
				e.doit = false;
			}
		}

	};

	private final Listener fListener = new Listener() {

		public void handleEvent(Event event) {
			final Control ra = (Control) event.widget;
			if (!ra.isDisposed()) {
				ra.removeTraverseListener(ColumnEditingSupport.this.traverseListener);
				ra.removeKeyListener(ColumnEditingSupport.this.keyListener);
				ra.removeListener(SWT.FocusOut, this);
			}

		}

	};

	private void registerListeners(Control control) {
		if (control instanceof Composite) {
			final Composite a = (Composite) control;
			for (final Control c : a.getChildren()) {
				this.registerListeners(c);
			}
		}

		control.addTraverseListener(this.traverseListener);
		control.addKeyListener(this.keyListener);
		control.addListener(SWT.FocusOut, this.fListener);
	}

	



	

	public void setValueAndContinue(Object element, Object value) {
		final ListEnumeratedValueSelector<Object> ownerSelector = (ListEnumeratedValueSelector<Object>) this.column.getOwnerSelector();
		final ColumnViewer viewer2 = (ColumnViewer) ownerSelector.getViewer();
		final ISelection sl = viewer2.getSelection();
		// ownerSelector.getControl().setRedraw(false);
		ownerSelector.setIgnoreChanges(true);
		ownerSelector.getViewer().setSelection(new StructuredSelection());
		this.setValue(element, value);
		viewer2.editElement(element, this.columnIndex);
		// ownerSelector.getControl().setRedraw(true);
		ownerSelector.setIgnoreChanges(false);
		ownerSelector.getViewer().setSelection(sl);
	}

	@Override
	protected Object getValue(Object element) {
		return column.getElement(element);
	}

	@Override
	protected void setValue(Object element, Object value) {
		column.setValue(element, value);
	}
}