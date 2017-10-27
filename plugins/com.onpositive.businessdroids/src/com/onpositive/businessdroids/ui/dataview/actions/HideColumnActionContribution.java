package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.Collection;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class HideColumnActionContribution extends ActionContribution {

	protected final IColumn column;
	protected final StructuredDataView dataView;

	public HideColumnActionContribution(Drawable icon, IColumn column,
			StructuredDataView dataView) {
		super("Hide column", icon);
		this.column = column;
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return this.column.getVisible() != IColumn.INVISIBLE;
	}

	@Override
	public void run() {
		Collection<IColumn> visibleColumns = this.dataView.getVisibleColumns();
		visibleColumns.remove(this.column);
		this.dataView.setVisibleColumns(visibleColumns);
	}

}
