package com.onpositive.semantic.model.ui.actions;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;

/**
 * {@link IContributionManager} extension having a method for getting
 * action list depending on current selection
 * @author Dmitry Karpenko
 */
public interface IObjectContributionManager extends IContributionManager {
	
	IContributionItem[] getItems(IStructuredSelection selection);
	
	public void add(IContributionItem item, Class<?> targetClass);
	
	public void addAfter(String id, IContributionItem item, Class<?> targetClass);
	
}
