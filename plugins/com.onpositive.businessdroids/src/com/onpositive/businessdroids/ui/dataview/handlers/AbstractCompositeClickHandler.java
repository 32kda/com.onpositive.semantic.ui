package com.onpositive.businessdroids.ui.dataview.handlers;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItemProvider;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;


public class AbstractCompositeClickHandler {

	protected IContributionItemProvider defaultContributionItemProvider;
	protected final StructuredDataView dataView;

	public AbstractCompositeClickHandler(StructuredDataView view) {
		super();
		this.dataView = view;
	}

	protected Collection<IContributionItem> getEnabledContributions(
			final IColumn column) {
		LinkedHashSet<IContributionItem> items = new LinkedHashSet<IContributionItem>();
		List<IContributionItem> contributionItems = this.defaultContributionItemProvider
				.getContributionItemsFor(column);
		List<IContributionItem> contributions = column.getContributions();
		if (contributionItems != null) {
			for (IContributionItem i : contributionItems) {
				if (i.isEnabled()) {
					items.add(i);
				}
			}
		}
		if (contributions != null) {
			for (IContributionItem i : contributions) {
				if (i.isEnabled()) {
					items.add(i);
				}
			}
			// items.addAll(contributions);
		}
		return items;
	}

	public IContributionItemProvider getDefaultContributionItemProvider() {
		return this.defaultContributionItemProvider;
	}

	public void setDefaultContributionItemProvider(
			IContributionItemProvider defaultContributionItemProvider) {
		this.defaultContributionItemProvider = defaultContributionItemProvider;
	}
}