package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.actions.IObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManagerExtension;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

class HierarchyController extends ElementListenerAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Context context;
	protected ElementWrapper element;
	protected final String parentId;
	protected Object parentContext;

	protected boolean toMenu;
	protected boolean toToolbar;
	
	protected IProvidesToolbarManager toolbarManager;
	protected IContributionManager popupMenuManager;

	protected IContributionItem action;
	protected IContributionItem item;
	protected Class<?> targetClass;
	protected final AbstractActionElementHandler parentHandler;

	protected HierarchyController(Element element, Object parentContext,
			Context context, AbstractActionElementHandler parentHandler) {
		this.context = context;
		this.parentHandler = parentHandler;
		this.parentId = element.getAttribute("targetId");
		this.element =new ElementWrapper(element);
		this.parentContext = parentContext;

		this.toToolbar = Boolean.parseBoolean(((Element) element
				.getParentNode()).getAttribute("toToolbar"))
				|| (parentContext instanceof IProvidesToolbarManager);
		String attribute = element.getAttribute("toToolbar");
		if (attribute.length() > 0) {
			toToolbar = Boolean.parseBoolean(attribute);
		}
		this.toMenu = Boolean.parseBoolean(((Element) element
				.getParentNode()).getAttribute("toMenu"));
		attribute = element.getAttribute("toMenu");
		if (attribute.length() > 0) {
			toMenu = Boolean.parseBoolean(attribute);
		}
		String targetClassName = element.getAttribute("targetClass");
		if (targetClassName.length() > 0) {
			try {
				targetClass = context.getClassLoader().loadClass(targetClassName);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void hierarchyChanged(IUIElement<?> uielement) {
		doHandle();
	}

	public void doHandle() {

		IUIElement<?> parentUI;
		if (parentId != null && parentId.length() > 0) {
			ICompositeElement<?, ?> root = (ICompositeElement<?, ?>) ((BasicUIElement<?>) parentContext)
					.getRoot();
			if (root == null)
				root = (ICompositeElement<?, ?>) parentContext;
			parentUI = root.getElement(parentId); // out of actions element
													// case
		} else
			parentUI = (IUIElement<?>) parentContext;// TODO need errors
														// processing here
		if (parentUI == null) {
			return;
		}


		IProvidesToolbarManager newToolbarManager = null;
		IContributionManager newPopupMenuManager = null;

		doInternal(parentUI, newToolbarManager, newPopupMenuManager);
	}
	
	protected void doInternal(IUIElement<?> parentUI,
			IProvidesToolbarManager newToolbarManager, IContributionManager newPopupMenuManager) {
		boolean toolbarChanged = false;
		boolean menuChanged = false;
		boolean needNewAction = false;
		if (this.toToolbar) {
			newToolbarManager = parentUI
					.getService(IProvidesToolbarManager.class);
			toolbarChanged = this.toolbarManager != newToolbarManager;
			needNewAction = needNewAction || toolbarChanged;
		}
		if (this.toMenu) {
			newPopupMenuManager = parentUI.getPopupMenuManager();
			menuChanged = this.popupMenuManager != newPopupMenuManager;
			needNewAction = needNewAction || menuChanged;
		}
		boolean onlyExport=false;
		if (element.getAttribute("exportAs").length()>0&&item==null){
			if (!this.toMenu&&!this.toToolbar){
				if (parentUI instanceof IPropertyEditor){
					IPropertyEditor<?> m=(IPropertyEditor<?>)parentUI;
					if (m.getBinding()!=null){
					needNewAction=true;
					onlyExport=true;
					}
				}
			}
		}
		if (needNewAction) {
			IContributionItem newAction = parentHandler.contribute(new ActionsElementHandler.ActionsSetting(
					parentUI, true), context, element);
			if (onlyExport){
				item=newAction;
			}
			if (toolbarChanged) {
				if (this.toolbarManager != null)
					removeFromToolbar(this.action);

				if (newToolbarManager != null && newAction != null) {
					this.toolbarManager = newToolbarManager;
					addToToolbar(newAction);
				}
			}
			if (menuChanged) {
				if (this.popupMenuManager != null)
					removeFromMenu(this.action);

				if (newPopupMenuManager != null && newAction != null) {
					this.popupMenuManager = newPopupMenuManager;
					addToMenu(newAction);
				}
			}
			this.action = newAction;
		}
	}

	protected void addToMenu(IContributionItem newAction) {
		if (targetClass != null && popupMenuManager instanceof IObjectContributionManager) {
			((IObjectContributionManager)popupMenuManager).add(newAction,targetClass);
		} else {
			this.popupMenuManager.add(newAction);
		}
	}

	protected void removeFromMenu(IContributionItem action) {
		this.popupMenuManager.remove(action);
	}

	protected void removeFromToolbar(IContributionItem action) {
		this.toolbarManager.removeFromToolbar(action);
	}

	protected void addToToolbar(IContributionItem newAction) {
		if (targetClass != null && toolbarManager instanceof IProvidesToolbarManagerExtension) {
			((IProvidesToolbarManagerExtension)toolbarManager).addToToolbar(newAction,targetClass);
		} else {
			this.toolbarManager.addToToolbar(newAction);
		}
	}

}
