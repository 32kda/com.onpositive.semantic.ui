package com.onpositive.semantic.ui.workbench.providers;

import com.onpositive.semantic.model.api.undo.IBasicUndoProvider;
import com.onpositive.semantic.model.api.undo.IUndoManager;
import com.onpositive.semantic.model.api.undo.IUndoProvider;
import com.onpositive.semantic.model.ui.actions.IContributionItem;

public class WorkbenchUndoRedoProvider implements IUndoProvider,IBasicUndoProvider{

	@Override
	public IContributionItem createUndoAction() {
		return null;
	}

	@Override
	public IContributionItem createRedoAction() {
		return null;
	}


	@Override
	public Object getGlobalUndoContext() {		
		return null;
	}

	@Override
	public IUndoManager getUndoChangeManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
