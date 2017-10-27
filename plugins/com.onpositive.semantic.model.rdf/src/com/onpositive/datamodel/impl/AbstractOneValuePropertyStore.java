package com.onpositive.datamodel.impl;

import java.util.Collections;
import java.util.Set;

import com.onpositive.datamodel.core.BatchChange;
import com.onpositive.datamodel.core.DataStoreChange;
import com.onpositive.datamodel.core.IDataStoreListener;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public abstract class AbstractOneValuePropertyStore extends
		AbstractPropertyStore {

	protected String property;

	public AbstractOneValuePropertyStore(String property) {
		this.property = property;
	}

	public void batchChange(BatchChange parameterObject) {
		HashDelta<Object> dlt2 = new HashDelta<Object>();
		HashDelta<IEntry>dlt = new HashDelta<IEntry>();
		dlt2.markChanged(property);
		for (DataStoreChange c : parameterObject.changes) {
			if (c.property.equals(property)) {
				setValues(c.entry, property, c.newValues);				
				dlt.markChanged(c.entry,dlt2);
			}
		}
		for (IDataStoreListener l:listeners){
			l.dataStoreChanged(this, dlt);
		}
	}

	public boolean contains(String id) {
		return false;
	}
	
	public IEntry getEntry(String id) {
		return null;
	}
	
	public IRealm<IEntry> getEntities() {
		Realm<IEntry> realm = new Realm<IEntry>();
		return realm;
	}
	
	public void removeEntry(IEntry e) {

	}

	public Set<String> getKnownProperties() {
		return Collections.singleton(property);
	}
	
	public String getProperty(){
		return property;
	}
}
