package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class ReplaceColumnsActionContribution extends
		AbstractCompositeContributionItem {

	protected final IColumn column;
	protected final StructuredDataView dataView;

	public ReplaceColumnsActionContribution(Drawable icon, IColumn column,
			StructuredDataView dataView) {
		super("Replace column", icon);
		this.column = column;
		this.dataView = dataView;
	}
	public ReplaceColumnsActionContribution(String text,Drawable icon, IColumn column,
			StructuredDataView dataView) {
		super(text, icon);
		this.column = column;
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		Collection<IColumn> visibleColumns = this.dataView.getVisibleColumns();
		return (this.column==null||visibleColumns.contains(this.column))
				&& (this.dataView.getColumns().length > visibleColumns.size());
	}

	@Override
	public IContributionItem[] getChildren() {
		IColumn[] columns = this.dataView.getColumns();
		Collection<IColumn> visibleColumns = this.dataView.getVisibleColumns();
		ArrayList<IColumn> possibleColumns = new ArrayList<IColumn>();
		for (IColumn column : columns) {
			if (!visibleColumns.contains(column)) {
				possibleColumns.add(column);
			}
		}
		ArrayList<IContributionItem> resultList = new ArrayList<IContributionItem>();
		for (IColumn toShow : possibleColumns) {
			resultList.add(new ReplaceColumnActionContribution(this.icon,
					this.dataView, this.column, toShow));
		}
		return resultList.toArray(new IContributionItem[0]);
	}

}
