package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IGroupable;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;

public class AddFiltersListActionContribution extends
		AbstractCompositeContributionItem {

	protected final StructuredDataView dataView;
	protected final IFilterSetupVisualizer filterSetupVisualizer;

	public AddFiltersListActionContribution(String text, Drawable icon,
			StructuredDataView dataView,
			IFilterSetupVisualizer filterSetupVisualizer) {
		super(text, icon);
		this.dataView = dataView;
		this.filterSetupVisualizer = filterSetupVisualizer;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getText() {
		return "Filters add";
	}

	@Override
	public IContributionItem[] getChildren() {
		IColumn[] columns = this.dataView.getPresentationSortedColumns();
		ArrayList<IContributionItem> m = new ArrayList<IContributionItem>();
		for (IColumn column : columns) {
			List<IFilter> asList = Arrays.asList(column
					.getPossibleFiltersProvider().getPossibleFilters(
							this.dataView.getTableModel()));
			for (IFilter f : asList) {
				AddFilterActionContribution addFilterActionContribution = new AddFilterActionContribution(
						"", f, this.dataView, this.filterSetupVisualizer);
				if (column instanceof IGroupable) {
					IGroupable z = (IGroupable) column;
					addFilterActionContribution.setGroupId(z.getGroup());
				}
				m.add(addFilterActionContribution);
			}
		}

		IContributionItem[] result = m.toArray(new IContributionItem[m.size()]);
		return result;
	}

}
