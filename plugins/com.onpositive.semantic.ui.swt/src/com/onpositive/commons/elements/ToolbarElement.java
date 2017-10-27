package com.onpositive.commons.elements;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.property.editors.structured.ContributionItemConverter;
import com.onpositive.semantic.ui.core.Alignment;

public class ToolbarElement extends AbstractUIElement<ToolBar> implements
		IProvidesToolbarManager {

	private final ToolBarManager tManager = new ToolBarManager(SWT.HORIZONTAL);

	public ToolbarElement() {
		this.getLayoutHints().setGrabVertical(false);
		this.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
	}

	protected ToolBar createControl(Composite conComposite) {
		final ToolBar tm = this.tManager.createControl(conComposite);
		tm.setBackgroundMode(SWT.INHERIT_FORCE);
		// tm.setBackground(conComposite.getBackground());
		return tm;
	}

	public ToolBarManager getToolbarManager() {
		return this.tManager;
	}

	public void addToToolbar(IAction action) {
		this.tManager.add(action);
		this.tManager.update(false);
	}

	public void addToToolbar(IContributionItem item) {
		this.tManager.add(item);
		this.tManager.update(false);
	}

	public IContributionItem remove(IContributionItem item) {
		final IContributionItem remove = this.tManager.remove(item);
		this.tManager.update(false);
		return remove;
	}

	public IContributionItem remove(String ID) {
		final IContributionItem remove = this.tManager.remove(ID);
		this.tManager.update(false);
		return remove;
	}

	public void removeAll() {
		this.tManager.removeAll();
	}

	public void removeFromToolbar(IAction action) {
		this.tManager.remove(new ActionContributionItem(action));
	}

	

	public void addToToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem bindedAction) {
		tManager.add(ContributionItemConverter.from(bindedAction));
	}

	public void removeFromToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem action) {
		tManager.remove(ContributionItemConverter.from(action));
	}
}
