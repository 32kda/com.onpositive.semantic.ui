package com.onpositive.businessdroids.ui.dataview.handlers;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.actions.SortAction;

import android.view.View;


public class SortHeaderClickHandler implements ITablePartClickHandler {

	protected Map<IColumn, SortAction> actions = new HashMap<IColumn, SortAction>();
	protected TableModel tableModel;

	public SortHeaderClickHandler(TableModel tableModel) {
		super();
		this.tableModel = tableModel;
	}

	@Override
	public void handleClick(IColumn column, View source) {
		if (!this.actions.containsKey(column)) {
			this.actions.put(column, new SortAction(this.tableModel, column));
		}
		this.actions.get(column).run();

	}

}
