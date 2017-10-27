package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.handlers.ITopLevelText;

public class AddFilterActionContribution extends ActionContribution implements
		ITopLevelText ,IExtendedContributionItem{

	protected IFilter filter;
	protected StructuredDataView dataView;
	protected final IFilterSetupVisualizer filterSetupVisualizer;
	protected String groupId;

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public AddFilterActionContribution(String text, IFilter filter,
			StructuredDataView dataView,
			IFilterSetupVisualizer filterSetupVisualizer) {
		super(text, dataView.getCurrentTheme().getIconProvider()
				.getAddFilterIcon(dataView.getContext()));
		this.filter = filter;
		this.dataView = dataView;
		this.filterSetupVisualizer = filterSetupVisualizer;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void run() {
		this.filterSetupVisualizer.setupFilter(this.filter, this.dataView);
		// dataView.getTableModel().addFilter(filter);
	}

	@Override
	public String getText() {
		if (this.filter instanceof AbstractColumnFilter) {
			return ((AbstractColumnFilter) this.filter).getColumn().getId()
					+ " (" + this.filter.getTitle() + ")";
		}
		return this.filter.getTitle();
	}

	@Override
	public String getTopLevelText() {
		return "Add " + this.getText() + " filter";
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public int getPriority() {
		return 1;
	}

}
