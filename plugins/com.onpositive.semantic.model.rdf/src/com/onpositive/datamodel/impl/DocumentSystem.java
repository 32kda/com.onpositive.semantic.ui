package com.onpositive.datamodel.impl;

import java.io.IOException;
import java.util.HashMap;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IDataStore;
import com.onpositive.datamodel.model.DataModel;

public class DocumentSystem {

	private final DataStoreRealm ds;
	private final DataModel model;
	private final HashMap<IStorage, DocumentStorage> storages = new HashMap<IStorage, DocumentStorage>();
	private String id;

	public DocumentSystem(DataModel model) {
		this.ds = new DataStoreRealm(model);
		this.model = model;
	}

	public DataStoreRealm getRealm() {
		return this.ds;
	}

	public DataModel getDataModel() {
		return this.model;
	}
	
	public void addDataStore(IDataStore store){
		this.ds.addDataStore(store);
	}
	
	public void removeDataStore(IDataStore store){
		this.ds.removeDataStore(store);
	}
	

	public void addStorage(IStorage storage) throws IOException {
		final DocumentStorage documentStorage = new DocumentStorage(this, storage,this.ds);
		documentStorage.syncTimeOut=10000;
		documentStorage.setStoreAutomatically(true);
		documentStorage.setStoreImmediatly(false);
		this.storages.put(storage, documentStorage);
		this.ds.addDataStore(documentStorage.getDocument());
	}

	public void removeStorage(IStorage storage) {
		final DocumentStorage documentStorage = this.storages.get(storage);
		if (documentStorage.isDirty()) {
			documentStorage.saveDocument();
		}
		this.storages.remove(storage);
		this.ds.removeDataStore(documentStorage.getDocument());
	}

	public void handleException(IOException e, DocumentStorage documentStorage) {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
