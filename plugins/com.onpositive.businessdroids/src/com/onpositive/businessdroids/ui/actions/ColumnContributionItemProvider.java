package com.onpositive.businessdroids.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.aggregation.DateTimeAggregator;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IdentityAggregator;
import com.onpositive.businessdroids.model.aggregation.NumericAggregator;
import com.onpositive.businessdroids.model.aggregation.RangeAggregator;
import com.onpositive.businessdroids.model.filters.BasicStringFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.actions.AggregatorActionContibution;
import com.onpositive.businessdroids.ui.dataview.actions.FilterActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.FilterConfigurationContributionItem;
import com.onpositive.businessdroids.ui.dataview.actions.HideColumnActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.ListGroupingActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.RemoveFieldFiltersActionContributon;
import com.onpositive.businessdroids.ui.dataview.actions.ReplaceColumnsActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.SimpleUngroupingActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.SortActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.TwoStateGroupingActionContribution;
import com.onpositive.businessdroids.ui.dataview.actions.ViewConfigurationContributionItem;
import com.onpositive.businessdroids.ui.themes.IIconProvider;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.graphics.drawable.Drawable;


public class ColumnContributionItemProvider implements
		IContributionItemProvider {

	protected HashMap<IColumn, List<IContributionItem>> columnContributions = new HashMap<IColumn, List<IContributionItem>>();
	protected final StructuredDataView dataView;

	// protected IFilterSetupVisualizer filterSetupVisualizer;

	public ColumnContributionItemProvider(List<? extends IColumn> columns,
			StructuredDataView dataView) {
		this.dataView = dataView;
		for (IColumn column : columns) {
			this.columnContributions.put(column,
					this.getDefaultContributionItems(column, dataView));
		}

	}
	public ColumnContributionItemProvider(StructuredDataView dataView) {
		this.dataView = dataView;
		for (IColumn column : dataView.getColumns()) {
			this.columnContributions.put(column,
					this.getDefaultContributionItems(column, dataView));
		}

	}

	protected List<IContributionItem> getDefaultContributionItems(
			IColumn column, StructuredDataView dataView) {
		Class<?> type = column.getType();
		IFilterSetupVisualizer filterSetupVisualizer = dataView
				.getFilterSetupVisualizer();
		IIconProvider iconProvider = dataView.getCurrentTheme()
				.getIconProvider();
		Context context = dataView.getContext();
		final ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		Drawable groupIcon = iconProvider.getGroupIcon(context);
		Drawable ungroupIcon = iconProvider.getUngroupIcon(context);
		Drawable removeGroupIcon = ungroupIcon;
		Drawable sortUpIcon = iconProvider.getSortUpIcon(context);
		Drawable sortDownIcon = iconProvider.getSortDownIcon(context);
		IFilter[] possibleFilters = column.getPossibleFiltersProvider()
				.getPossibleFilters(dataView.getTableModel());
		if (column.isCaption()){
			result.add(new SortActionContribution(column, dataView, sortUpIcon,
					sortDownIcon));
			result.add(new ListGroupingActionContribution(groupIcon, ungroupIcon,
						this.dataView));
			result.add(new SimpleUngroupingActionContribution("Ungroup",
						ungroupIcon, this.dataView));
			result.add(new FilterActionContribution(dataView, possibleFilters,
					filterSetupVisualizer, null));
			result.add(new RemoveFieldFiltersActionContributon(column,
					dataView, iconProvider.getRemoveFilterIcon(context)));
		}
		else if (Number.class.isAssignableFrom(type)) {
			result.add(new SortActionContribution(column, dataView, sortUpIcon,
					sortDownIcon));
			
			result.add(new TwoStateGroupingActionContribution(column, dataView,
					groupIcon, removeGroupIcon));
			
			// ArrayList<IFilter> countFilters = new ArrayList<IFilter>();
			// countFilters.add(new
			// ComparableFilter(dataView.getTableModel(),field,null,null));
			// countFilters.add(new
			// ExplicitValueFilter(dataView.getTableModel(),field,null)); //TODO
			// test
			result.add(new FilterActionContribution(dataView, possibleFilters,
					filterSetupVisualizer, null));
			result.add(new RemoveFieldFiltersActionContributon(column,
					dataView, iconProvider.getRemoveFilterIcon(context)));
			List<IAggregator> countAggregators = new ArrayList<IAggregator>();
			countAggregators.add(new NumericAggregator());
			countAggregators.add(new RangeAggregator());
			countAggregators.add(IdentityAggregator.INSTANCE);
			result.add(new AggregatorActionContibution(iconProvider
					.getAggregateIcon(context), column, countAggregators,
					dataView));
		} else if (type.equals(String.class)) {
			result.add(new SortActionContribution(column, dataView, sortUpIcon,
					sortDownIcon));
			
			result.add(new TwoStateGroupingActionContribution(column, dataView,
					groupIcon, removeGroupIcon));
			result.add(new FilterActionContribution(dataView, possibleFilters,
					filterSetupVisualizer, null));
			result.add(new RemoveFieldFiltersActionContributon(column,
					dataView, iconProvider.getRemoveFilterIcon(context)));
		} else if (type.equals(Boolean.class)) {
			result.add(new SortActionContribution(column, dataView, sortUpIcon,
					sortDownIcon));
			result.add(new TwoStateGroupingActionContribution(column, dataView,
					groupIcon, removeGroupIcon));
			result.add(new FilterActionContribution(dataView, possibleFilters,
					filterSetupVisualizer, null));
			result.add(new RemoveFieldFiltersActionContributon(column,
					dataView, iconProvider.getRemoveFilterIcon(context)));
		} else if (type.equals(Date.class)) {
			result.add(new SortActionContribution(column, dataView, sortUpIcon,
					sortDownIcon));
			result.add(new TwoStateGroupingActionContribution(column, dataView,
					groupIcon, removeGroupIcon));
			List<IAggregator> dateAggregators = new ArrayList<IAggregator>();
			dateAggregators.add(new DateTimeAggregator());
			dateAggregators.add(IdentityAggregator.INSTANCE);
			result.add(new AggregatorActionContibution(iconProvider
					.getAggregateIcon(context), column, dateAggregators,
					dataView));
			result.add(new FilterActionContribution(dataView, possibleFilters,
					filterSetupVisualizer, null));
			result.add(new RemoveFieldFiltersActionContributon(column,
					dataView, iconProvider.getRemoveFilterIcon(context)));
		}
		if (!column.isAlwaysVisible()) {
			result.add(new HideColumnActionContribution(iconProvider
					.getHideColumnIcon(context), column, dataView));
			result.add(new ReplaceColumnsActionContribution(iconProvider
					.getReplaceColumnIcon(context), column, dataView));
		}
		Collections.sort(result, new Comparator<IContributionItem>() {

			@Override
			public int compare(IContributionItem object1,
					IContributionItem object2) {
				return object1.getId().compareTo(object2.getId());
			}
		});
		return result;
	}

	@Override
	public List<IContributionItem> getContributionItemsFor(IColumn column) {
		return this.columnContributions.get(column);
	}

	@Override
	public List<IContributionItem> getCommonContributionItems() {
		IFilterSetupVisualizer filterSetupVisualizer = this.dataView
				.getFilterSetupVisualizer();
		List<IFilter> filters = new ArrayList<IFilter>();
		BasicStringFilter stringFilter = null;
		IFilter[] registeredFilters = this.dataView.getTableModel()
				.getRegisteredFilters();
		for (IFilter filter : registeredFilters) {
			if (filter instanceof BasicStringFilter) {
				stringFilter = (BasicStringFilter) filter;
			}
		}
		if (stringFilter == null) {
			stringFilter = new BasicStringFilter(this.dataView, "",
					BasicStringFilter.CONTAIN_MODE);
		}
		filters.add(stringFilter);
		ITheme currentTheme = this.dataView.getCurrentTheme();
		Drawable filterIcon = currentTheme.getIconProvider().getSearchIcon(
				this.dataView.getContext());
		FilterActionContribution filterContribution = new FilterActionContribution(
				this.dataView, filters.toArray(new IFilter[0]),
				filterSetupVisualizer, filterIcon);
		List<IContributionItem> result = new ArrayList<IContributionItem>();
		result.add(filterContribution);
		Drawable settingsIcon = this.dataView.getCurrentTheme()
				.getIconProvider().getSettingsIcon(this.dataView.getContext());
		result.add(new ViewConfigurationContributionItem(settingsIcon,
				this.dataView));
		// result.add(new
		// ColumnVisibilityActionContribution(currentTheme.getIconProvider().getColumnsIcon(dataView.getContext()),
		// dataView));
		result.add(new FilterConfigurationContributionItem(this.dataView,
				filterSetupVisualizer));
		return result;
	}

}
