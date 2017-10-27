package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.actions.IContributionItem;

/**
 * This class also supports object action contribution into toolbar
 * @author Dmitry Karpenko
 *
 */
public interface IProvidesToolbarManagerExtension extends
		IProvidesToolbarManager {
	void addToToolbar(IContributionItem bindedAction, Class<?> targetClass);
}
