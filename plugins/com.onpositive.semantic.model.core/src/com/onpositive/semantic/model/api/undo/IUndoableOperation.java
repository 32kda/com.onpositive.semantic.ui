package com.onpositive.semantic.model.api.undo;


public interface IUndoableOperation {

	Object execute();

	Object undo();

	Object getUndoContext();

}
