package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IProvidesUI;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class ActionsElementHandler extends GeneralElementHandler {

	public ActionsElementHandler() {
		super(null, null);
	}

	
	public Object handleElement(Element element, Object parentContext,
			Context context) {
		evaluateChildren(element, parentContext, context);
		return null;
	}

	public static class ActionsSetting implements IProvidesUI{

		IUIElement<?> gui;
		Boolean toToolbar;

		public ActionsSetting(IUIElement<?> parentUI, boolean toToolbar) {
			this.gui = parentUI;
			this.toToolbar = toToolbar;
		}

		public void addAction(IContributionItem contributionItem, Element element) {
			boolean toToolbar = isToToolbar(element);
			if (toToolbar) {
				IProvidesToolbarManager provider = getProvider();
				if (provider != null)
					provider.addToToolbar(contributionItem);
			}
		}
		
		public void removeAction(IContributionItem contributionItem, Element element) {
			boolean toToolbar = isToToolbar(element);
			if (toToolbar) {
				IProvidesToolbarManager provider = getProvider();
				if (provider != null)
					provider.removeFromToolbar(contributionItem);
			}
		}

		protected IProvidesToolbarManager getProvider() {
			IProvidesToolbarManager provider;
			if (this.gui instanceof IProvidesToolbarManager)
				provider = (IProvidesToolbarManager) this.gui;
			else 
				provider = gui
						.getService(IProvidesToolbarManager.class);
			return provider;
		}

		protected boolean isToToolbar(Element element) {
			boolean toToolbar=this.toToolbar!=null?this.toToolbar:false;
			String attribute = element.getAttribute("toToolbar");
			if (attribute.length()!=0){
				toToolbar=Boolean.parseBoolean(attribute);
			}
			return toToolbar;
		}

		public IUIElement<?> getControl() {
			return gui;
		}

		public IUIElement<?> getUI() {
			return gui;
		}
	}

}