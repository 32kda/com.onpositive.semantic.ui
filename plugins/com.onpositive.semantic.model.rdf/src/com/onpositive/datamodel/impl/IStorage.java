package com.onpositive.datamodel.impl;

import java.io.IOException;

import com.onpositive.datamodel.core.IDocument;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.realm.ISetDelta;

public interface IStorage {

	void store(IDocument document) throws IOException;
	
	void setRealm(IDataStoreRealm realm);

	IDocument load() throws IOException;

	ISetDelta<IEntry> delta(IDocument current) throws IOException;

}