package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IGroupable;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;

public abstract class ColumnActionContribution extends ActionContribution
		implements IExtendedContributionItem {

	protected final IColumn column;
	protected final StructuredDataView dataView;

	public ColumnActionContribution(String text, Drawable icon, IColumn column,
			StructuredDataView dataView) {
		super(text, icon);
		this.column = column;
		this.dataView = dataView;
	}

	@Override
	public String getGroupId() {
		if (column instanceof IGroupable) {
			IGroupable g = (IGroupable) column;
			return g.getGroup();
		}
		return null;
	}

	@Override
	public int getPriority() {
		return 1;
	}
}
