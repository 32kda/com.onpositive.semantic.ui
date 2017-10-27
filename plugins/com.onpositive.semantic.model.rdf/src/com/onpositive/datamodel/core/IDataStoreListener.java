package com.onpositive.datamodel.core;

import com.onpositive.semantic.model.realm.ISetDelta;

public interface IDataStoreListener {

	void propertyChanged(IDataStore ds, IEntry e, String property,
			Object[] values);

	void entryAdding(IDataStore ds, IEntry entry);

	void entryRemoved(IDataStore ds, IEntry entry);

	void dataStoreChanged(IDataStore ds, ISetDelta<IEntry> entry);
}
