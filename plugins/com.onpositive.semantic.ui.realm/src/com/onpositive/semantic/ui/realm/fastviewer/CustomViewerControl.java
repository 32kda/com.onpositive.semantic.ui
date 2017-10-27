package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Table;

import com.onpositive.semantic.model.ui.property.editors.structured.ColumnLocking;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TableAdapter;
import com.onpositive.semantic.realm.Activator;

public class CustomViewerControl extends Composite implements IViewer {

	private FastTreeViewer fastTreeViewer;
	private Slider slider;
	private SavablePart part;

	public void addColumn(FastTreeColumn column) {
		fastTreeViewer.addColumn(column);
	}

	public void redraw() {
		fastTreeViewer.getControl().redraw();
		fastTreeViewer.getControl().update();
		super.redraw();
	}

	public void setSelector(ISelector selector) {
		fastTreeViewer.provider.setSelector(selector);
	}

	public Object getState() {
		return this.fastTreeViewer.provider.getState();
	}

	public void setState(Object object, Object object2) {
		this.fastTreeViewer.provider.setState(object, object2);
	}

	public CustomViewerControl(Composite parent, int style,
			final SavablePart part) {
		super(parent, style);
		this.part = part;
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		fastTreeViewer = new FastTreeViewer();
		fastTreeViewer.create(composite);
		composite.setLayout(new TableColumnLayout());
		//Composite cm = new Composite(this, SWT.NONE);
		//GridLayout layout2 = new GridLayout(2, false);
		//layout2.marginHeight = 0;
		//layout2.marginWidth = 0;
		//cm.setLayout(layout2);
		//Composite sliderBody = new Composite(cm, SWT.NONE);
		//slider = new Slider(sliderBody, SWT.HORIZONTAL);
		//slider.setLocation(0, 0);
		//GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		//layoutData.heightHint = 0;
		//sliderBody.setLayoutData(layoutData);
		//sliderBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridData layoutData2 = new GridData(GridData.FILL_HORIZONTAL);
		layoutData2.heightHint = 0;
		//cm.setLayoutData(layoutData2);
		//Composite buttons = new Composite(cm, SWT.NONE);
		//buttons.setLayout(new FillLayout(SWT.HORIZONTAL));
		//buttons.setLayoutData(new GridData(GridData.END, GridData.CENTER,
		//		false, false));
		//fastTreeViewer.setSlider(slider);
		fastTreeViewer.setSavablePart(part);
	}

	public void setInput(ITreeNode infiniteTreeNode) {
		fastTreeViewer.setInput(infiniteTreeNode);
	}

	public boolean isTree() {
		return true;
	}

	public void refresh() {

	}

	public void setInput(Object input) {

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fastTreeViewer.addSelectionChangedListener(listener);
	}

	public ISelection getSelection() {
		return fastTreeViewer.getSelection();
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {

	}

	public void setSelection(ISelection selection) {

	}

	public Control getControl() {
		return fastTreeViewer.getControl();
	}

	public void addDoubleClickListener(IDoubleClickListener doubleClickListener) {
		fastTreeViewer.addDoubleClickListener(doubleClickListener);
	}

	String oldId;
	boolean fireUpdate;
	boolean reconfiguring;

	public void configure(ViewerConfiguration configuration, boolean persist) {
		try{
		reconfiguring=true;
		this.setRedraw(false);
		this.setLayoutDeferred(true);
		fastTreeViewer.provider.captureSelection();
		final Table control = (Table) fastTreeViewer.getControl();
		if (oldId != null && persist) {
			PreferenceUtils.persistSettings(Activator.getDefault()
					.getPreferenceStore(), oldId, control);
		}
		try {
			fastTreeViewer.removeAllColumns();
			fastTreeViewer.setIsTree(configuration.isTree);
			for (int a = 0; a < configuration.columns.length; a++) {
				fastTreeViewer.addColumn(configuration.columns[a]);
			}
			String sortColumnId = configuration.sortColumnId;
			FastTreeColumn sColumn = null;
			for (int a = 0; a < configuration.columns.length; a++) {
				FastTreeColumn column = configuration.columns[a];
				if (column.getColumnId().equals(sortColumnId)) {
					sColumn = column;
				}
			}
			if (sColumn == null) {
				if (configuration.columns.length != 0) {
					sColumn = configuration.columns[0];
				}
			}
			fastTreeViewer.provider.inChange = true;
			try {
				if (sColumn != null) {
					fastTreeViewer.setSortColumn(sColumn,
							configuration.sortDirection);
				}
				if (configuration.isTree) {
					new ColumnLocking(new TableAdapter(control),
							new int[] { 0 });
				}
				final int colCount = control.getColumnCount();
				for (int i = 0; i < colCount; ++i) {
					control.getColumn(i).addListener(SWT.Move, new Listener() {

						public void handleEvent(Event event) {
							if (!reconfiguring){
							part.saveColumnOrder();
							}
						}

					});
				}
				int[] columnOrder = configuration.columnOrder;
				if (columnOrder != null) {
					if (columnOrder.length == ((Table) this.fastTreeViewer
							.getControl()).getColumnCount()) {
						((Table) this.fastTreeViewer.getControl())
								.setColumnOrder(columnOrder);
					}
				}
				this.layout(true, true);
			} finally {
				fastTreeViewer.provider.inChange = false;
			}
			if (configuration.state != null) {
				this.setState(configuration.state, configuration.viewerInput);
			} else {
				this.fastTreeViewer
						.setInput((ITreeNode) configuration.viewerInput);
			}
			PreferenceUtils.persist(Activator.getDefault()
					.getPreferenceStore(), control, configuration.id);
			oldId = configuration.id;

		} finally {

			this.setLayoutDeferred(false);
			this.setRedraw(true);
		}
		}finally{
			reconfiguring=false;
		}
	}

	public void addOpenListener(IOpenListener openListener) {
		fastTreeViewer.addOpenListener(openListener);
	}

	public DrillFrame goForward(Object sl) {
		DrillFrame goForward = fastTreeViewer.goForward((ITreeNode) sl);
		return goForward;
	}

	public void restore(DrillFrame frame) {
		this.fastTreeViewer.restore(frame);
	}

	public StructuredViewer getStructuredViewer() {
		return fastTreeViewer.getStructuredViewer();
	}

	public void removeColumn(FastTreeColumn column) {
		fastTreeViewer.removeColumn(column);
	}

	public int[] getColumnOrder() {
		return ((Table) this.fastTreeViewer.getControl()).getColumnOrder();
	}

	public void setColumnOrder(int[] order) {
		((Table) this.fastTreeViewer.getControl()).setColumnOrder(order);
	}

	public void expand() {
		fastTreeViewer.expand();
	}

	public void collapseAll() {
		fastTreeViewer.collapseAll();
	}

	public void dispose() {
		part = null;
		super.dispose();
	}

	public void setExpandFirst(boolean b) {
		this.fastTreeViewer.provider.setExpandFirst(b);
	}

	public Object getInput() {
		return fastTreeViewer.provider.getInput();
	}

	public void setContentCallback(Runnable runnable) {
		fastTreeViewer.setContentCallback(runnable);
	}

	public FastTreeColumn[] getColumns() {
		List columns = fastTreeViewer.getColumns();
		FastTreeColumn[] fastTreeColumns = new FastTreeColumn[columns.size()];
		columns.toArray(fastTreeColumns);
		return fastTreeColumns;
	}

	public void removeColumn(String id) {
		FastTreeColumn[] columns = getColumns();
		for (FastTreeColumn c:columns){
			if (c.getColumnId().equals(id)){
				removeColumn(c);
				return;
			}
		}
	}
}
