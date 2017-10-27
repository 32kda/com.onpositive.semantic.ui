package com.onpositive.semantic.model.api.undo;


public interface IUndoManager {

	public Object execute(IUndoableOperation undoable);

}
