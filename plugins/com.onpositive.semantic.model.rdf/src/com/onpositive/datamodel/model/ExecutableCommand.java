package com.onpositive.datamodel.model;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.semantic.model.api.command.CompositeCommand;

public class ExecutableCommand extends CompositeCommand{

	final DataStoreRealm realm;
	final boolean undoable;
	
	public ExecutableCommand(DataStoreRealm realm,
			boolean undoable) {
		super();
		this.realm = realm;
		this.undoable = undoable;
	}
	
	public DataStoreRealm getRealm() {
		return realm;
	}
	public boolean isUndoable() {
		return undoable;
	}

	public void execute(){
		realm.execute(this);
	}
		
}
