package com.onpositive.ui.simpleapp;

import java.util.List;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	@Override
	protected void fillCoolBar(ICoolBarManager coolBarManager) {
//		List<ActionElement> actions = ContributionHelper.getApplicationElement().getActions();
//		IToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
//		coolBarManager.add(new ToolBarContributionItem(toolBarManager, "main"));
//		MenuManager menuManager = (MenuManager) getActionBarConfigurer().getMenuManager();
//		for (ActionElement actionElement : actions) {
//			ActionUtil.contributeAction(toolBarManager,menuManager,ContributionHelper.getContext().getClass().getClassLoader(),actionElement,this);
//		}
		coolBarManager.update(true);
//	        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
//	        coolBar.add(new ToolBarContributionItem(toolbar, "main"));  
//	        toolbar.add(new Action("ѕицот") {
//			});
//	        toolbar.add(new Action("ѕицотјдин") {
//			});
		super.fillCoolBar(coolBarManager);
	}

}
