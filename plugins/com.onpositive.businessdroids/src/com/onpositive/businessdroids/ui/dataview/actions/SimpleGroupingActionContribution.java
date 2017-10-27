package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.groups.NumericRangeGroupingCalculator;
import com.onpositive.businessdroids.model.groups.RoughDateGroupingCalculator;
import com.onpositive.businessdroids.model.groups.SimpleFieldGroupingCalculator;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;
import android.text.format.Time;

public class SimpleGroupingActionContribution extends ColumnActionContribution
		implements IExtendedContributionItem {

	public static final int DEFAULT_RANGE_COUNT = 5;
	protected IGroupingCalculator groupingCalculator;

	public SimpleGroupingActionContribution(String text, Drawable icon,
			IColumn column, StructuredDataView dataView) {
		super("Group by " + getTitle(column), icon, column, dataView);
		IGroupingCalculator tCalc = column.getGroupingCalculator();
		if (tCalc != null) {
			this.groupingCalculator = tCalc;
			return;
		}
		groupingCalculator = createGroupingCalculator(column);
	}

	protected static String getTitle(IColumn column) {
		String title = column.getTitle();
		if (title == null || title.length() == 0)
			title = column.getId();
		return title;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void run() {
		TableModel tableModel = this.dataView.getTableModel();
		if (this.groupingCalculator != null) {
			IGroupingCalculator groupingCalculator2 = tableModel
					.getCurrentGroupingCalculator();
			if (groupingCalculator2 != this.groupingCalculator) {
				addColumn();
				tableModel.setCurrentGrouping(this.groupingCalculator);
			}
		} else {
			tableModel.setCurrentGrouping(null);
		}

	}

	protected void addColumn() {
		if (column.addWhenGrouped()) {
			Collection<IColumn> visibleColumns = dataView.getVisibleColumns();
			if (visibleColumns.size() == 1 && column.getType() != String.class) {
				ArrayList<IColumn> columns = new ArrayList<IColumn>();
				columns.addAll(visibleColumns);
				columns.add(column);
				dataView.setVisibleColumns(columns);
			}
		}
	}

	protected IGroupingCalculator createGroupingCalculator(IColumn column) {
		if (Number.class.isAssignableFrom(column.getType())) {
			return new NumericRangeGroupingCalculator(column,
					SimpleGroupingActionContribution.DEFAULT_RANGE_COUNT);
		} else if (Date.class.isAssignableFrom(column.getType())) {
			return new RoughDateGroupingCalculator(column);
		} else if (Time.class.isAssignableFrom(column.getType())) {
			return new RoughDateGroupingCalculator(column);
		} else
			return new SimpleFieldGroupingCalculator(column);
	}

}
