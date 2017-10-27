package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class ListGroupingActionContribution extends
		AbstractCompositeContributionItem {

	protected final StructuredDataView dataView;
	protected final Drawable groupIcon;
	protected final Drawable ungroupIcon;

	public ListGroupingActionContribution(Drawable groupIcon,
			Drawable ungroupIcon, StructuredDataView dataView) {
		super("", groupIcon);
		this.dataView = dataView;
		this.groupIcon = groupIcon;
		this.ungroupIcon = ungroupIcon;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getText() {
		return "Group by";
	}

	@Override
	public Drawable getIcon() {
		// Drawable groupIcon =
		// dataView.getCurrentTheme().getIconProvider().getGroupIcon(dataView.getContext());
		return this.groupIcon;
	}

	@Override
	public IContributionItem[] getChildren() {
		List<IContributionItem> result = new ArrayList<IContributionItem>();
		TableModel tableModel = this.dataView.getTableModel();
		IField column = null;
		IGroupingCalculator calculator = tableModel.getCurrentGroupingCalculator();
		if (calculator != null && calculator instanceof IFieldGroupingCalculator) {
			column = ((IFieldGroupingCalculator) calculator).getGroupField();
		}
		IColumn[] columns = this.dataView.getPresentationSortedColumns();
		Drawable groupIcon = this.dataView.getCurrentTheme().getIconProvider()
				.getGroupIcon(this.dataView.getContext());
		for (IColumn column2 : columns) {
			if (column2 != column&&column2.isGroupable()) {
				result.add(new SimpleGroupingActionContribution("Group by "
						+ column2.getId(), groupIcon, column2, this.dataView));
			}
		}
		if (column != null) {
			result.add(new SimpleUngroupingActionContribution("Ungroup",
					this.ungroupIcon, this.dataView));
		}
		return result.toArray(new IContributionItem[0]);
	}

}
