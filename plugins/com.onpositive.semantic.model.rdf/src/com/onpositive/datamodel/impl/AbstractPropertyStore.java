package com.onpositive.datamodel.impl;

import java.util.Arrays;
import java.util.HashSet;

import com.onpositive.datamodel.core.IDataStore;
import com.onpositive.datamodel.core.IDataStoreListener;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.realm.IRealm;

public abstract class AbstractPropertyStore implements IDataStore {

	protected HashSet<IDataStoreListener> listeners = new HashSet<IDataStoreListener>();
	protected IRealm<IEntry> dRealm;

	public void addDataStoreListener(IDataStoreListener listener) {
		listeners.add(listener);
	}

	public void removeDataStoreListener(IDataStoreListener listener) {
		listeners.remove(listener);
	}

	public IRealm<IEntry> getRealm() {
		if (this.dRealm != null) {
			return this.dRealm;
		}
		return null;
	}

	public void setRealm(IDataStoreRealm ra) {
		this.dRealm = ra;
	}

	public boolean contains(IEntry e) {
		return contains(e.getId());
	}

	public boolean containsAbout(String property) {
		return getKnownProperties().contains(property);
	}
	
	
	public String getUrl() {
		return "";
	}

	public Object getValue(IEntry e, String property) {
		Object[] values = getValues(e, property);
		if (values==null||values.length==0){
			return null;
		}
		return values[0];
	}
	

	public boolean hasValue(IEntry e, String property, Object value) {
		Object[] values = getValues(e, property);
		if (values==null){
			return false;
		}
		return Arrays.asList(values).contains(value);
	}


	public void setValue(IEntry e, String property, Object value) {
		setValues(e, property, value);
	}

	
}