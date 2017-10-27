package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class TwoStateGroupingActionContribution extends
		SimpleGroupingActionContribution {

	protected Drawable groupIcon;
	protected Drawable ungroupIcon;

	public TwoStateGroupingActionContribution(IColumn column,
			StructuredDataView dataView, Drawable groupIcon,
			Drawable removeGroupIcon) {
		super("", groupIcon, column, dataView);
		this.groupIcon = groupIcon;
		this.ungroupIcon = removeGroupIcon;

	}

	@Override
	public String getText() {
		TableModel tableModel = this.dataView.getTableModel();
		if (this.groupingCalculator != null) {
			IGroupingCalculator groupingCalculator2 = tableModel
					.getCurrentGroupingCalculator();
			if (!this.groupingCalculator.equals(groupingCalculator2)) {
				return "Group by " + this.column.getId();
			} else {
				return "Ungroup";
			}
		} 
		return "Group by " + this.column.getId();
//		else {
//			if (this.dataView.getCurrentGroupColumn() != this.column) {
//				return "Group by " + this.column.getId();
//			} else {
//				return "Ungroup";
//			}
//		}
	}

	// @Override
	// public Drawable getIcon() {
	// boolean on = false;
	// TableModel tableModel = dataView.getTableModel();
	// if (groupingCalculator != null)
	// on = groupingCalculator == tableModel.getCurrentGroupingCalculator();
	// else
	// on = tableModel.getCurrentGroupField() == this.field;
	// if (on || action)
	// return
	// ThemeManager.getCurrentTheme().getSelectedQuickActionBackgroundDrawable();
	// else
	// return ThemeManager.getCurrentTheme().getQuickActionBackgroundDrawable();
	// }

	@Override
	public void run() {
		TableModel tableModel = this.dataView.getTableModel();
		if (this.groupingCalculator != null) {
			IGroupingCalculator groupingCalculator2 = tableModel
					.getCurrentGroupingCalculator();
			if (!this.groupingCalculator.equals(groupingCalculator2)) {
				addColumn();
				tableModel
						.setCurrentGrouping(this.groupingCalculator);
			} else {
				tableModel.setCurrentGrouping(null);
			}
		} else {
			tableModel.setCurrentGrouping(null);
			
		}

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Drawable getIcon() {
		TableModel tableModel = this.dataView.getTableModel();
		if (this.groupingCalculator != null) {
			IGroupingCalculator groupingCalculator2 = tableModel
					.getCurrentGroupingCalculator();
			if (!this.groupingCalculator.equals(groupingCalculator2)) {
				return this.groupIcon;
			} else {
				return this.ungroupIcon;
			}
		} else {
			return this.groupIcon;
		}
	}

	public Drawable getGroupIcon() {
		return this.groupIcon;
	}

	public void setGroupIcon(Drawable groupIcon) {
		this.groupIcon = groupIcon;
	}

	public Drawable getRemoveGroupIcon() {
		return this.ungroupIcon;
	}

	public void setRemoveGroupIcon(Drawable removeGroupIcon) {
		this.ungroupIcon = removeGroupIcon;
	}

}
