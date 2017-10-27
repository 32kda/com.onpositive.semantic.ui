package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.ColumnLayoutData;
import com.onpositive.semantic.model.ui.generic.widgets.ITableElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ColumnLocking;
import com.onpositive.semantic.model.ui.property.editors.structured.IRealmContentProvider;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.TreeAdapter;
import com.onpositive.viewer.extension.coloring.ColoredViewersManager;

public class TableEnumeratedValueSelector extends
		ListEnumeratedValueSelector<Object> implements ITableElement<Control>{

	
	public void recreate() {
		getControl().setRedraw(false);
		Layout layout2 = getControl().getParent().getLayout();
		getControl().getParent().setLayout(null);
		super.recreate();
		getControl().setRedraw(false);
		getControl().getParent().setLayout(layout2);
		getControl().getParent().layout(true, true);
		getControl().setRedraw(true);
		// Display.getCurrent().asyncExec(new Runnable() {
		//			
		// public void run() {
		// if (getControl()!=null&&!getControl().isDisposed()){
		// getControl().setRedraw(true);
		// }
		// }
		// });
	}

	private final class BC extends ViewerSorter {
		private final Column column;

		private BC(Column column) {
			this.column = column;
		}

		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {
			int c = column.getRealComparator().compare(e1, e2);
			return TableEnumeratedValueSelector.this.sortDirection ? c : -c;
		}
	}

	private ArrayList<Column> columns = new ArrayList<Column>();
	protected Column sortColumn;
	protected boolean sortDirection;
	private Layout layout;
	private int[] columnOrder;
	private boolean headerVisible = true;
	private boolean linesVisible = true;
	private Column editingColumn;

	private ColumnLocking locking;
	private String lockedColumn;

	public void dispose() {
		if (this.locking != null) {
			this.locking = null;
		}
		super.dispose();
	}

	protected void initComparator(IRealmContentProvider realmLazyContentProvider) {

	}

	protected void lockColumn(String columnId) {
		this.lockedColumn = columnId;
		if (getViewer() != null) {
			if (lockedColumn != null) {
				int ma = -1;
				int a = 0;
				for (Column c : columns) {
					if (c.getId().equals(columnId)) {
						ma = a;
						break;
					}
					a++;
				}
				if (ma != -1) {
					columnOrder = getColumnOrder();
					// int pos = columnOrder[0];
					columnOrder[0] = ma;
					for (int b = 1; b < columnOrder.length; b++) {
						if (b != ma) {
							columnOrder[b] = b;
						} else {
							columnOrder[b] = 0;
						}
					}
					columnOrder[ma] = 0;

					if (this.locking != null) {
						locking.dispose();
					}
					setColumnOrder(columnOrder);
					int[] lockedColumnIdxs = new int[] { ma };
					createLocking(lockedColumnIdxs);
				}
			} else {
				if (locking != null) {
					locking.dispose();
				}
				createLocking(new int[0]);
			}
		}
	}

	private void createLocking(int[] lockedColumnIdxs) {
		if (isAsTree()) {
			locking = new ColumnLocking(new TreeAdapter((Tree) getViewer()
					.getControl()), lockedColumnIdxs);

		} else {
			locking = new ColumnLocking(new TableAdapter((Table) getViewer()
					.getControl()), lockedColumnIdxs);
		}
		locking.addOrderListener(new Observer() {

			public void update(Observable o, Object arg) {
				int[] order = (int[]) arg;
				initFirst(order);
				viewer.refresh(true);
				viewer.getControl().getDisplay().asyncExec(new Runnable() {

					public void run() {
						if (viewer != null) {
							Control control = viewer.getControl();
							if (!control.isDisposed()) {
								control.redraw();
							}
						}
					}

				});
			}

		});
		initFirst(getColumnOrder());
	}

	protected void initSorting(IRealm<Object> realm) {

	}

	public TableEnumeratedValueSelector(Binding binding) {
		super(binding);
	}

	public TableEnumeratedValueSelector() {

	}

	protected void handleChange(Object extraData) {
		super.handleChange(extraData);
		for (final Column c : this.columns) {
			c.changed(extraData);
		}
	}

	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		final String clm = configuration.getStringAttribute("scolumn");
		if (clm != null) {
			for (final Column m : this.columns) {
				if ((m.getId() != null) && m.getId().equals(clm)) {
					this.sortColumn = m;
					break;
				}
			}
		}

		this.sortDirection = configuration.getBooleanAttribute("sdirection");
		final String cOrder = configuration.getStringAttribute("corder");
		if ((cOrder != null) && (cOrder.length() > 0)) {
			final String[] elements = cOrder.split(",");
			final int[] order = new int[elements.length];
			for (int a = 0; a < elements.length; a++) {
				order[a] = Integer.parseInt(elements[a].trim());
			}
			if (order.length == this.columns.size()) {
				this.columnOrder = order;
			} else if (Platform.getOS().equals("linux")) {
				if (order.length == this.columns.size() + 1) {
					this.columnOrder = order;
				}
			}
		}
		final String width = configuration.getStringAttribute("column-width");
		if ((width != null) && (width.length() > 0)) {
			final String[] widthes = width.split(",");
			if (widthes.length == this.columns.size()) {
				for (int a = 0; a < widthes.length; a++) {
					final int wi = Integer.parseInt(widthes[a]);
					if (wi != -1) {
						this.columns.get(a).setLayoutData(
								new ColumnLayoutData(1, wi,true));
					}
				}
			}
		}
		super.loadConfiguration(configuration);
	}

	public void internalStoreConfiguration(IAbstractConfiguration configuration) {
		if ((this.sortColumn != null) && (this.sortColumn.getId() != null)) {
			configuration
					.setStringAttribute("scolumn", this.sortColumn.getId());
			configuration.setBooleanAttribute("sdirection", this.sortDirection);
		}
		final int[] cOrder = this.getColumnOrder();
		StringBuilder bld = new StringBuilder();
		for (int a = 0; a < cOrder.length; a++) {
			bld.append(cOrder[a]);
			if (a != cOrder.length - 1) {
				bld.append(',');
			}
		}
		configuration.setStringAttribute("corder", bld.toString());
		bld = new StringBuilder();
		for (int a = 0; a < this.columns.size(); a++) {
			bld.append(this.columns.get(a).getWidth());
			if (a != this.columns.size() - 1) {
				bld.append(',');
			}
		}
		configuration.setStringAttribute("column-width", bld.toString());
		super.storeConfiguration(configuration);
	}

	public void setColumnOrder(int[] columnOrder) {
		try {
			if (this.isAsTreeActually()) {
				((Tree) this.getViewer().getControl())
						.setColumnOrder(columnOrder);
				this.columnOrder = columnOrder;
			} else {
				((Table) this.getViewer().getControl())
						.setColumnOrder(columnOrder);
				this.columnOrder = columnOrder;
			}
		} catch (Exception e) {

		}
	}

	public int[] getColumnOrder() {
		if (this.getViewer() != null) {
			final Control control = this.getViewer().getControl();
			if (this.isAsTreeActually()) {
				return ((Tree) control).getColumnOrder();
			} else {
				return ((Table) control).getColumnOrder();
			}
		} else {
			return this.columnOrder;
		}
	}

	protected void initLabelProvider(ITextLabelProvider pr) {
		if (pr != null) {
			if (pr instanceof ILabelProvider) {
				final ILabelProvider llp = (ILabelProvider) pr;
				this.viewer.setLabelProvider(llp);
			} else {
				TableLabelProvider tableLabelProvider = new TableLabelProvider(
						this, pr);
				final ColumnLabelProvider ll = tableLabelProvider;
				tableLabelProvider.setRowStyleProvider(this
						.getRowStyleProvider());
				this.viewer.setLabelProvider(ll);
			}
		} else {
			TableLabelProvider labelProvider = new TableLabelProvider(this,
					null);
			labelProvider.setRowStyleProvider(this.getRowStyleProvider());
			this.viewer.setLabelProvider(labelProvider);
		}
	}

	@ChildSetter( value="column" , needCasting=false )
	public void addColumn(Column column) {
		if (this.isCreated()) {
			this.internalAddColumn(this.columns.size(), column);
		}
		this.columns.add(column);
	}

	public void addColumn(int index, Column column) {
		if (this.isCreated()) {
			this.internalAddColumn(index, column);
		}
		this.columns.add(index, column);
	}

	private void internalAddColumn(int index, Column column) {
		if (this.isAsTreeActually()) {
			column.setOwnerSelector(this);
			column.setController(new TreeColumnController(
					(TreeViewer) this.viewer, new TreeColumn((Tree) this
							.getViewer().getControl(), SWT.NONE, index),
					this.layout));
			this.adapt(column);
		} else {
			column.setOwnerSelector(this);
			column.setController(new TableColumnController(
					(TableViewer) this.viewer, new TableColumn((Table) this
							.getViewer().getControl(), SWT.NONE, index),
					this.layout));
			this.adapt(column);
		}
	}

	private void adapt(final Column column) {
		Listener listener = new Listener(){

			public void handleEvent(Event event) {
				if (event.type==SWT.Move){
				if (isAsTree()){
					TreeViewer t=(TreeViewer) viewer;
					columnOrder=t.getTree().getColumnOrder();
				}
				else{
					TableViewer t=(TableViewer) viewer;
					columnOrder=t.getTable().getColumnOrder();
				}
				}
				else{
					sortChanged(column);	
				}
			}
			
		};
		((AbstractController)column.getController()).item.addListener(SWT.Move,listener);
		((AbstractController)column.getController()).item.addListener(SWT.Selection,listener);		
	}

	private void installNewColumns(Collection<Column> columns2, final Viewer viewer)
	{
		for (final Column c : columns2) {
			c.setOwnerSelector(this);
			c.setController(this.createController(viewer));
			this.adapt(c);
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {

			public void run() {
				TableEnumeratedValueSelector.this.installSort(viewer);
			}
		});
	}

	public Control createControl(Composite parent) {
		Control control = super.createControl(parent);
		lockColumn(lockedColumn);
		final Composite c = (Composite) getViewer().getControl();
		c.getVerticalBar().addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				c.redraw();
			}

			public void widgetSelected(SelectionEvent e) {
				c.redraw();
			}

		});
		return control;
	}

	protected AbstractController createController(Viewer viewer) {
		if (this.isAsTreeActually()) {
			return new TreeColumnController((TreeViewer) viewer,
					new TreeColumn((Tree) viewer.getControl(), SWT.NONE),
					this.layout);
		} else {
			return new TableColumnController((TableViewer) viewer,
					new TableColumn((Table) viewer.getControl(), SWT.NONE),
					this.layout);
		}
	}

	IOpenListener openListener = new IOpenListener() {

		public void open(OpenEvent event) {
			
			if (TableEnumeratedValueSelector.this.isAllowCellEditing()) {
				final StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				if (sel.size() == 1) {
					int index = 0;
					if (TableEnumeratedValueSelector.this.editingColumn != null) {
						index = TableEnumeratedValueSelector.this.columns
								.indexOf(TableEnumeratedValueSelector.this.editingColumn);
						if (index == -1) {
							index = 0;
						}
					}
					((ColumnViewer) TableEnumeratedValueSelector.this
							.getViewer()).editElement(sel.getFirstElement(),
							index);
				}
				
			}
			for (IOpenListener l:openListeners){
				l.open(event);
			}
		}
	};

	private boolean imageOnFirstColumn;

	public boolean isImageOnFirstColumn() {
		return imageOnFirstColumn;
	}

	protected void configureViewer(TableViewer newCheckList) {
		ColoredViewersManager.install(newCheckList);
		final TableColumnLayout layout = new TableColumnLayout();
		final ColumnViewerEditorActivationStrategy columnViewerEditorActivationStrategy = new ActivationStrategy(
				newCheckList);
		TableViewerEditor.create(newCheckList,
				columnViewerEditorActivationStrategy,
				ColumnViewerEditor.DEFAULT);
		newCheckList.addOpenListener(this.openListener);
		this.layout = layout;
		final Table table = newCheckList.getTable();
		final Composite parent2 = table.getParent();
		parent2.setLayout(layout);
		this.installNewColumns(this.columns, newCheckList);
		table.setHeaderVisible(this.headerVisible);
		table.setLinesVisible(this.linesVisible);
		final String os = Platform.getOS();

		if (os.equals("linux")) {
			if (this.columns.get(this.columns.size() - 1).isMovable()) {
				final TableColumn tableColumn = new TableColumn(newCheckList
						.getTable(), SWT.NONE);
				layout
						.setColumnData(tableColumn, new ColumnPixelData(5,
								false));
			}
		}
		if (this.columnOrder != null) {
			if (table.getColumnCount() == this.columnOrder.length) {
				table.setColumnOrder(this.columnOrder);
			}
		}

	}

	protected int getSelectionStyle() {
		return super.getSelectionStyle() | SWT.FULL_SELECTION;
	}

	HashSet<Object> getExpandedElements = new HashSet<Object>();

	public Collection<Object> getExpandedElements() {
		return new HashSet<Object>(getExpandedElements);
	}

	public void setExpandedElements(Collection<Object> elements) {
		StructuredViewer viewer2 = getViewer();
		getExpandedElements.clear();
		getExpandedElements.addAll(elements);
		if (viewer2 != null && viewer2 instanceof TreeViewer) {
			TreeViewer tv = (TreeViewer) viewer2;
			tv.setExpandedElements(elements.toArray());
		}
	}

	protected void configureViewer(TreeViewer newCheckList) {
		ColoredViewersManager.install(newCheckList);
		final TreeColumnLayout layout = new TreeColumnLayout();
		newCheckList.addOpenListener(this.openListener);
		final ColumnViewerEditorActivationStrategy columnViewerEditorActivationStrategy = new ActivationStrategy(
				newCheckList);
		TreeViewerEditor.create(newCheckList,
				columnViewerEditorActivationStrategy,
				ColumnViewerEditor.DEFAULT);
		newCheckList.addTreeListener(new ITreeViewerListener() {

			public void treeExpanded(TreeExpansionEvent event) {
				getExpandedElements.add(event.getElement());
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				getExpandedElements.remove(event.getElement());
			}
		});
		this.layout = layout;
		final Tree tree = newCheckList.getTree();
		final Composite parent2 = tree.getParent();
		parent2.setLayout(layout);
		this.installNewColumns(this.columns, newCheckList);
		tree.setHeaderVisible(this.headerVisible);
		tree.setLinesVisible(this.linesVisible);
		if (Platform.getOS().equals("linux")) {
			if (this.columns.get(this.columns.size() - 1).isMovable()) {
				final TreeColumn tableColumn = new TreeColumn(newCheckList
						.getTree(), SWT.NONE);
				layout
						.setColumnData(tableColumn, new ColumnPixelData(5,
								false));
			}
		}
		if (this.columnOrder != null) {
			if (this.getColumnCount() >= this.columnOrder.length) {
				try {
					tree.setColumnOrder(this.columnOrder);
				} catch (Exception e) {

				}
			}
		}
	}

	public List<Column> getColumns() {
		return new ArrayList<Column>(this.columns);
	}

	public void removeColumn(Column column) {
		if (this.columns.remove(column)) {
			((AbstractController)column.getController()).dispose();
		}
	}

	public void setColumns(List<Column> columns) {
		if (this.isCreated()) {
			this.getViewer().getControl().setRedraw(false);
			for (final Column c : columns) {
				((AbstractController)c.getController()).dispose();
			}
			this.installNewColumns(columns, this.getViewer());
			this.getViewer().getControl().setRedraw(true);
		}
		this.columns = new ArrayList<Column>(columns);
	}

	public boolean isHeaderVisible() {
		return this.headerVisible;
	}

	public void setHeaderVisible(boolean headerVisible) {
		this.headerVisible = headerVisible;
		if (this.isCreated()) {
			if (this.isAsTreeActually()) {
				final Tree tc = (Tree) this.getViewer().getControl();
				tc.setHeaderVisible(headerVisible);
			} else {
				final Table tc = (Table) this.getViewer().getControl();
				tc.setHeaderVisible(headerVisible);
			}
		}
	}

	public boolean isLinesVisible() {
		return this.linesVisible;
	}

	public void setLinesVisible(boolean linesVisible) {
		this.linesVisible = linesVisible;
		if (this.isCreated()) {
			if (this.isAsTreeActually()) {
				final Tree tc = (Tree) this.getViewer().getControl();
				tc.setLinesVisible(linesVisible);
			} else {
				final Table tc = (Table) this.getViewer().getControl();
				tc.setLinesVisible(linesVisible);
			}
		}
	}

	private boolean isAsTreeActually() {
		if (this.isCreated()) {
			return this.getViewer().getControl() instanceof Tree;
		} else {
			return this.isAsTree();
		}
	}

	public Column getColumn(int ci) {
		return this.columns.get(ci);
	}

	@SuppressWarnings("unchecked")
	protected void installSort(final Viewer viewer) {
		if (this.sortColumn == null) {
			if (this.columns.size() > 0) {
				this.sortColumn = this.columns.get(0);
			}
		}
		if (this.sortColumn != null) {
			(((AbstractController)sortColumn.getController())).setSortColumn(this.sortDirection);
			final IRealmContentProvider cp = (IRealmContentProvider) ((ColumnViewer) viewer)
					.getContentProvider();
			if (cp != null) {
				cp.setComparator(this.sortColumn.getRealComparator(),
						this.sortDirection);
				viewer.getControl().getParent().layout(true);
			}
		}
	}

	protected void packColumnsIfNeeded() {

	}

	public int getColumnCount() {
		return this.columns.size();
	}

	public void setEditingColumn(Column column) {
		this.editingColumn = column;
	}

	public void setImageOnFirstColumn(boolean parseBoolean) {
		this.imageOnFirstColumn = parseBoolean;
	}

	private void initFirst(int[] order) {
//		for (int a = 0; a < order.length; a++) {
//			columns.get(order[a]).setFirst(a == 0);
//		}
	}

	protected void sortChanged(final Column column) {
		final IContentProvider contentProvider = TableEnumeratedValueSelector.this.viewer
				.getContentProvider();
		if (TableEnumeratedValueSelector.this.sortColumn == column) {
			TableEnumeratedValueSelector.this.sortDirection = !TableEnumeratedValueSelector.this.sortDirection;
			((AbstractController)column.getController()).setSortColumn(
					TableEnumeratedValueSelector.this.sortDirection);
			if (contentProvider instanceof IRealmContentProvider) {
				final IRealmContentProvider cp = (IRealmContentProvider) contentProvider;
				cp
						.setComparator(
								column.getRealComparator(),
								TableEnumeratedValueSelector.this.sortDirection);
			} else {
				viewer.setSorter(new BC(column));
			}
		} else {
			TableEnumeratedValueSelector.this.sortDirection = false;
			if (contentProvider instanceof IRealmContentProvider) {
				final IRealmContentProvider cp = (IRealmContentProvider) contentProvider;
				cp.setComparator( column.getRealComparator(),
								TableEnumeratedValueSelector.this.sortDirection);
			} else {
				viewer.setSorter(new BC(column));
			}
			((AbstractController)column.getController()).setSortColumn(
					TableEnumeratedValueSelector.this.sortDirection);
			TableEnumeratedValueSelector.this.sortColumn = column;
		}
	}
}