package com.onpositive.datamodel.core;

import java.util.HashSet;
import java.util.Set;

import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.realm.IRealm;

public class ReadOnlyDataStore implements IDataStore { //XXX: Seems to be deadcode

	IRealm<IEntry> realm;

	public void batchChange(BatchChange parameterObject) {

	}

	public boolean contains(IEntry e) {
		return false;
	}

	public boolean contains(String id) {
		return false;
	}

	public boolean containsAbout(String property) {
		return false;
	}

	public IRealm<IEntry> getEntities() {
		return null;
	}

	public void getEntriesPointingTo(String propId, IEntry value,
			HashSet<IEntry> result) {

	}

	public void getEntriesWith(String property, Set<IEntry> toFill) {

	}

	public IEntry getEntry(String id) {
		return null;
	}

	public String getUrl() {
		return null;
	}

	public Object getValue(IEntry e, String property) {
		return null;
	}

	public Object[] getValues(IEntry e, String property) {
		return null;
	}

	public boolean hasValue(IEntry e, String property, Object value) {
		return false;
	}

	public IEntry newEntry() {
		return null;
	}

	public void removeDataStoreListener(IDataStoreListener listener) {

	}

	public void addDataStoreListener(IDataStoreListener listener) {

	}

	public void removeEntry(IEntry e) {
		throw new UnsupportedOperationException();
	}

	public void setRealm(IDataStoreRealm ra) {
		this.realm = ra;
	}

	public void setValue(IEntry e, String property, Object value) {
		throw new UnsupportedOperationException();
	}

	public void setValues(IEntry e, String property, Object... values) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getKnownProperties() {
		return null;
	}

	public void getEntriesWithValue(String propId, Object value,
			Set<IEntry> result) {
		
	}

}
