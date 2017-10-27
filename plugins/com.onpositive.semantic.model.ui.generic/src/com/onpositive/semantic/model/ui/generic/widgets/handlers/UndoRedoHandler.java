package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.api.undo.support.UndoRedoSupport;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.Separator;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class UndoRedoHandler extends AbstractActionElementHandler {

	public UndoRedoHandler() {
	}

	protected IContributionItem contribute(ActionsSetting parentContext, Context context, Element element) {
		parentContext.addAction( new Separator(), element );
		IContributionItem undoAction = (IContributionItem) UndoRedoSupport
				.createUndoAction();
		parentContext.addAction(undoAction,element);
		IContributionItem redoAction = (IContributionItem) UndoRedoSupport
				.createRedoAction();
//		parentContext.addAction(redoAction,element);
//		parentContext.addRetargetAction(UndoRedoSupportExtension
//				.createUndoAction());
//		parentContext.addRetargetAction(UndoRedoSupportExtension
//				.createRedoAction());
		//parentContext.addItem(new Separator());
		return redoAction;
	}

	
	protected void contribute(IProvidesToolbarManager parentContext,
			Context context, Element element) {
		parentContext.addToToolbar(new Separator());
		parentContext.addToToolbar((IContributionItem) UndoRedoSupport
				.createUndoAction());
		parentContext.addToToolbar((IContributionItem) UndoRedoSupport
				.createRedoAction());
		parentContext.addToToolbar(new Separator());
	}

}
