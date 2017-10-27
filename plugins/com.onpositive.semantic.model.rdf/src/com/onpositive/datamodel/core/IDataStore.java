package com.onpositive.datamodel.core;

import java.util.HashSet;
import java.util.Set;

import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.realm.IRealm;

public interface IDataStore {

	IRealm<IEntry> getEntities();

	String getUrl();

	public boolean contains(IEntry e);

	void setValues(IEntry e, String property, Object... values);

	Object[] getValues(IEntry e, String property);

	Object getValue(IEntry e, String property);

	boolean hasValue(IEntry e, String property, Object value);

	void setValue(IEntry e, String property, Object value);

	boolean containsAbout(String property);

	void getEntriesWith(String property, Set<IEntry> toFill);

	void batchChange(BatchChange parameterObject);

	void removeEntry(IEntry e);

	boolean contains(String id);

	IEntry getEntry(String id);

	void setRealm(IDataStoreRealm ra);

	void addDataStoreListener(IDataStoreListener listener);

	void removeDataStoreListener(IDataStoreListener listener);

	void getEntriesPointingTo(String propId, IEntry value,
			HashSet<IEntry> result);

	Set<String> getKnownProperties();
	
	void getEntriesWithValue(String propId, Object value,
			final Set<IEntry> result);

	
}
