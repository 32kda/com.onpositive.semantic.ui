package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.actions.IContributionItem;

/**
 * This interface represents API for some control wrappers, which can contain their own toolbar and
 * provide it's functionality for child wrappers/controls
 * (c) OnPositive
 * @author kor
 */

public interface IProvidesToolbarManager {

	void addToToolbar(IContributionItem bindedAction);
	
	void removeFromToolbar(IContributionItem action);	
}
