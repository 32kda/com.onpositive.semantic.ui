package com.onpositive.datamodel.model;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.realm.HashDelta;

public class CalculatablePropertyHost extends ProxyProperty{

	private ICalculatableProperty property;

	public CalculatablePropertyHost(ICalculatableProperty prop, ICommandExecutor executor,
			DefaultModelProperty parent, DataStoreRealm updater) {
		super(executor, parent, updater,prop);
		this.property=prop;
		property.setHost(this);
	}
	
	public IDataStoreRealm getRealm(){
		return realm;
	}
	
	public void fireDelta(HashDelta<?>dlt){
		realm.fireDelta(dlt);
	}

	
	public boolean isReadOnly() {
		return true;
	}
	
}
