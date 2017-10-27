package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.graphics.drawable.Drawable;


public class SortActionContribution extends ColumnActionContribution {

	protected SortAction action;
	// protected ArrowDrawable sortArrowDrawable;
	protected TableModel tableModel;
	protected Drawable ascendingIcon;
	protected Drawable descendingIcon;

	public SortActionContribution(IColumn column, StructuredDataView dataView,
			Drawable ascendingIcon, Drawable descendingIcon) {
		super("Sort by " + column.getId(), ascendingIcon, column, dataView);
		this.ascendingIcon = ascendingIcon;
		this.descendingIcon = descendingIcon;
		this.tableModel = dataView.getTableModel();
		// sortArrowDrawable =
		// ThemeManager.getCurrentTheme().getArrowDrawable(field);
		this.action = new SortAction(this.tableModel, column);
	}

	// @Override
	// public Drawable getIcon() {
	// ITheme currentTheme = ThemeManager.getCurrentTheme();
	// if (tableModel.getSortField() == field)
	// {
	//
	// Drawable actionBackgroundDrawable =
	// currentTheme.getSelectedQuickActionBackgroundDrawable();
	// BitmapDrawable bitmapDrawable;
	// if (action.getState() == SortAction.DESCENDING)
	// bitmapDrawable = new
	// BitmapDrawable(currentTheme.getSortedDownBitmap(dataView.getContext()));
	// else
	// bitmapDrawable = new
	// BitmapDrawable(currentTheme.getSortedUpBitmap(dataView.getContext()));
	// bitmapDrawable.setGravity(Gravity.CENTER);
	// LayerDrawable drawable = new LayerDrawable(new
	// Drawable[]{actionBackgroundDrawable, bitmapDrawable});
	// return drawable;
	// } else {
	// Drawable actionBackgroundDrawable =
	// currentTheme.getQuickActionBackgroundDrawable();
	// BitmapDrawable bitmapDrawable = new
	// BitmapDrawable(currentTheme.getUnsortedBitmap(dataView.getContext()));
	// bitmapDrawable.setGravity(Gravity.CENTER);
	// LayerDrawable drawable = new LayerDrawable(new
	// Drawable[]{actionBackgroundDrawable, bitmapDrawable});
	// return drawable;
	// }
	// }

	@Override
	public void run() {
		this.action.run();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Drawable getIcon() {
		ITheme currentTheme = this.dataView.getCurrentTheme();
		if ((this.dataView.getSortField() != this.column)
				|| (this.action.getState() == SortAction.DESCENDING)) {
			return currentTheme.getIconProvider().getSortUpIcon(
					this.dataView.getContext());
		} else {
			return currentTheme.getIconProvider().getSortDownIcon(
					this.dataView.getContext());
		}
	}

	@Override
	public String getText() {
		String text2 = super.getText();
		if ((this.dataView.getSortField() != this.column)
				|| (this.action.getState() == SortAction.DESCENDING)) {
			return text2 + " (Ascending)";
		} else {
			return text2 + " (Descending)";
		}
	}

	public Drawable getAscendingIcon() {
		return this.ascendingIcon;
	}

	public void setAscendingIcon(Drawable ascendingIcon) {
		this.ascendingIcon = ascendingIcon;
	}

	public Drawable getDescendingIcon() {
		return this.descendingIcon;
	}

	public void setDescendingIcon(Drawable descendingIcon) {
		this.descendingIcon = descendingIcon;
	}

}
