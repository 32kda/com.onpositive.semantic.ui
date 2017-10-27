package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.IIconProvider;

import android.graphics.drawable.Drawable;

public class ViewConfigurationContributionItem extends
		AbstractCompositeContributionItem {

	protected final StructuredDataView dataView;

	public ViewConfigurationContributionItem(Drawable icon,
			StructuredDataView dataView) {
		super("", icon);
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return true; // Always enabled
	}

	@Override
	public String getText() {
		return "View configuration";
	}

	@Override
	public IContributionItem[] getChildren() {
		List<IContributionItem> items = new ArrayList<IContributionItem>();
		IIconProvider iconProvider = this.dataView.getCurrentTheme()
				.getIconProvider();
		Drawable columnIcon = iconProvider.getColumnsIcon(this.dataView
				.getContext());
		Drawable groupIcon = iconProvider.getGroupIcon(this.dataView
				.getContext());
		Drawable ungroupIcon = iconProvider.getUngroupIcon(this.dataView
				.getContext());
		Drawable themeIcon = iconProvider.getThemeIcon(this.dataView
				.getContext());
		IColumn[] columns = dataView.getColumns();
		if (columns.length > 1) {
			if (columns.length > 10) {
				items.add(new ReplaceColumnsActionContribution("Add column",
						columnIcon,null, this.dataView));
			} else {
				items.add(new ColumnVisibilityActionContribution(columnIcon,
						this.dataView));
			}
		}
		items.add(new ListGroupingActionContribution(groupIcon, ungroupIcon,
				this.dataView));
		items.add(new SimpleUngroupingActionContribution("Ungroup",
				ungroupIcon, this.dataView));
		items.add(new SelectThemeContribution(themeIcon, this.dataView));
		return items.toArray(new IContributionItem[0]);
	}

}
