package com.onpositive.semantic.model.ui.actions;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;


public interface IActionContributor {

	void contributeActions(IContributionManager manager,
			IStructuredSelection selection, String role);
}
