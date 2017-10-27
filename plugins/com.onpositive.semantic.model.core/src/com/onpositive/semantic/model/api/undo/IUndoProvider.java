package com.onpositive.semantic.model.api.undo;



public interface IUndoProvider extends IBasicUndoProvider {

	//TODO FIX ME
	Object createUndoAction();

	Object createRedoAction();

	@Override
	IUndoManager getUndoChangeManager();

	Object getGlobalUndoContext();
}
