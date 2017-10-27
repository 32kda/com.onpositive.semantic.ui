package com.onpositive.semantic.ui.android;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.widgets.ITableElement;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;
import com.onpositive.semantic.ui.businessdroids.ColumnBridge;
import com.onpositive.semantic.ui.businessdroids.QueryBasedTableModel;
import com.onpositive.semantic.ui.businessdroids.QueryBasedTableModel2;

public class AndroidTable2 extends AndroidList2 implements ITableElement<View> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -615064801677392239L;
	private IColumn[] initialColumns;
	private List<Column> genericColumns;
	
	public AndroidTable2() {
		getLayoutHints().setGrabVertical(true);
	}

	protected AndroidTable2(IBinding binding) {
		super(binding);
		getLayoutHints().setGrabVertical(true);
	}

	@Override
	public void setHeaderVisible(boolean headerVisible) {
		StructuredDataView dataView = (StructuredDataView) getControl();
		dataView.setHeaderVisible(headerVisible);
	}

	@Override
	public void setLinesVisible(boolean linesVisible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLinesVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHeaderVisible() {
		StructuredDataView dataView = (StructuredDataView) getControl();
		return dataView.isHeaderVisible();
	}

	@Override
	public void setColumns(List<Column> columns) {
		this.genericColumns = columns;
		if (getControl() != null) {
			QueryBasedTableModel model = (QueryBasedTableModel) ((StructuredDataView) getControl()).getTableModel();
			model.setColumns(convertColumns(columns));
		} else {
			initialColumns = convertColumns(columns);
		}
	}
	
	@Override
	protected List<Column> getColumnsList() {
		return (List<Column>) genericColumns;
	}
	
	@Override
	public int getColumnCount(){		
		return initialColumns != null ? initialColumns.length : 0;
	}

	private IColumn[] convertColumns(List<Column> columns) {
		IColumn[] result = new IColumn[columns.size()];
		for (int i = 0; i < result.length; i++) {
			Column col = columns.get(i);			
			result[i] = new ColumnBridge( col, this );
			col.setOwnerSelector(this) ;
		}
		boolean hasCaption = false;
		IColumn stringColumn = null;
		for (int i = 0; i < result.length; i++) {
			Class<?> type = result[i].getType();
			if (type == Object.class)
				type = String.class;
			if (stringColumn == null && String.class.equals(type)){
				stringColumn = result[i];
			}
			if ("title".equalsIgnoreCase(result[i].getTitle()) || "caption".equalsIgnoreCase(result[i].getTitle())) { //XXX: dirty hack
				((ColumnBridge)result[i]).setCaption(true);
			}
			if (result[i].isCaption()) {
				hasCaption = true;
				break;
			}
		}
		if (!hasCaption) {
			if (stringColumn == null && result.length > 0) {
				stringColumn = result[0];
			}
			if (stringColumn != null) {
				((ColumnBridge)stringColumn).setCaption(true);
			}
		}
		return result;
	}
	
	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		if (initialColumns != null) {
			QueryBasedTableModel2 tableModel = new QueryBasedTableModel2(initialColumns,createDefaultExecutor());
			this.dataSource = tableModel;
			initContainer(null);
			StructuredDataView dataView = new StructuredDataView(context, tableModel);
			configureDataView(dataView);
			return dataView;
		}
		return super.internalCreate(cm, context);
	}
	
}
