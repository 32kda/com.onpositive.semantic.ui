package com.onpositive.businessdroids.ui.actions;

import java.util.List;

import com.onpositive.businessdroids.model.IColumn;


public interface IContributionItemProvider {

	public List<IContributionItem> getCommonContributionItems();

	public List<IContributionItem> getContributionItemsFor(IColumn column);

}
