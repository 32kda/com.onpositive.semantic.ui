package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IProvidesUI;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class PopupHandler extends GeneralElementHandler {

	private final class MenuHandler implements IProvidesUI,	IProvidesToolbarManager
	{
		private final IUIElement<?> mn;
		private MenuHandler(IUIElement<?> mn) {
			this.mn = mn;
		}

		public IUIElement<?> getUI() {
			return this.mn;
		}

		public void addToToolbar(IContributionItem bindedAction) {
			this.mn.getPopupMenuManager().add(bindedAction);
		}
		
		public void removeFromToolbar(IContributionItem action) {
			this.mn.getPopupMenuManager().remove(action);
		}
	}

	public PopupHandler(){
		super(null,null) ;
	}

	
	protected Object produceNewInstance(Element element, Object parentContext, Context context)
	{		
		final IUIElement<?> mn = (IUIElement<?>) parentContext;
		//mn.setCreatePopupMenu(true);
		return new MenuHandler(mn);		
	}
	
	
	protected Object returnedObject( Object newInstance ){
		return null ;
	}
}
