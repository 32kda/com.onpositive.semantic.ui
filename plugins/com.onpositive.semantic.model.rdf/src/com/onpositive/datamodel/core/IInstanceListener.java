package com.onpositive.datamodel.core;

import java.util.Map;

import com.onpositive.datamodel.core.DataStoreRealm.ObjectChange;
import com.onpositive.datamodel.impl.IDataStoreRealm;

public interface IInstanceListener {

  Map<IEntry, ObjectChange>processDelta(IDataStoreRealm realm,Map<IEntry,ObjectChange> ch);	
}
