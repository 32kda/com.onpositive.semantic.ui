package com.onpositive.semantic.model.data;


import com.onpositive.semantic.model.api.command.CompositeCommand;

public class ExecutableCommand extends CompositeCommand{

	final IDataStoreRealm realm;
	final boolean undoable;
	
	public ExecutableCommand(IDataStoreRealm realm,
			boolean undoable) {
		super();
		this.realm = realm;
		this.undoable = undoable;
	}
	
	public IDataStoreRealm getRealm() {
		return realm;
	}
	public boolean isUndoable() {
		return undoable;
	}

	public void execute(){
		realm.execute(this);
	}
		
}
