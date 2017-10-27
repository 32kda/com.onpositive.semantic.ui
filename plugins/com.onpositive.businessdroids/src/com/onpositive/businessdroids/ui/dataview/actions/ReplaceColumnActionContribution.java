package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.Collection;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IGroupable;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class ReplaceColumnActionContribution extends ActionContribution implements IExtendedContributionItem{

	protected final StructuredDataView dataView;
	protected final IColumn toHide;
	protected final IColumn toShow;

	public ReplaceColumnActionContribution(Drawable icon,
			StructuredDataView dataView, IColumn toHide, IColumn toShow) {
		super("Show " + toShow.getTitle(), icon);
		this.dataView = dataView;
		this.toHide = toHide;
		this.toShow = toShow;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void run() {
		Collection<IColumn> visibleColumns = this.dataView.getVisibleColumns();
		if (toHide!=null){
		visibleColumns.remove(this.toHide);
		}
		visibleColumns.add(this.toShow);
		this.dataView.setVisibleColumns(visibleColumns);
	}

	@Override
	public String getGroupId() {
		if (toShow instanceof IGroupable){
			IGroupable g=(IGroupable) toShow;
			return g.getGroup();
		}
		return null;
	}

	@Override
	public int getPriority() {
		return 1;
	}

}
