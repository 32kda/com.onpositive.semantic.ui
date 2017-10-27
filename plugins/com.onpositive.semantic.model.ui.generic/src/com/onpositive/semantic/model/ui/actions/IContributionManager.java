package com.onpositive.semantic.model.ui.actions;


public interface IContributionManager {

	void add(IContributionItem item);
	
	void addAfter(String id,IContributionItem item);

	void remove(IContributionItem action);

	IContributionItem[] getItems();
}
