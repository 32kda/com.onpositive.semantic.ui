package com.onpositive.datamodel.impl;

import java.util.Collection;
import java.util.Set;

import com.onpositive.datamodel.core.IDataStore;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.core.DataStoreRealm.ObjectChange;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.realm.IIdentifiableRealm;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.ITypedRealm;

public interface IDataStoreRealm extends IObjectRealm<IEntry>,
		IIdentifiableRealm<IEntry> {

	Collection<IDataStore> getStores();

	ITypedRealm<IEntry> getTypeRealm(IType type);

	ITypedRealm<IEntry> getTypeRealm(String type);

	void disconnectTypeRealm(IRealm<IEntry> tr);

	IType getType(String string);

	Set<IEntry>findEntries(String propertyId,Object value);
	
	ObjectChange newObjectChange(IEntry r);
}
