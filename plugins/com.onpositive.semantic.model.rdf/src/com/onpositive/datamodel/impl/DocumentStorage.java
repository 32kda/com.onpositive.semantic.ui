package com.onpositive.datamodel.impl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IDataStore;
import com.onpositive.datamodel.core.IDataStoreListener;
import com.onpositive.datamodel.core.IDocument;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.realm.EmptyDelta;
import com.onpositive.semantic.model.realm.ISetDelta;

public class DocumentStorage {

	private final IStorage storage;
	private IDocument document;

	private boolean storeAutomatically;
	private boolean storeImmediatly = true;
	private final DocumentSystem system;

	long syncTimeOut;
	private DataStoreRealm realm;

	static Timer storeTimer;

	public static synchronized void sheduleStore(long timeOut,
			final DocumentStorage storage) {
		if (storeTimer == null) {
			storeTimer = new Timer();
		}
		storeTimer.schedule(new TimerTask() {

			public void run() {
				storage.saveDocument();
			}

		}, timeOut);
	}

	public DocumentStorage(DocumentSystem documentSystem, IStorage storage, DataStoreRealm dataStoreRealm)
			throws IOException {
		this.storage = storage;
		this.system = documentSystem;
		this.realm=dataStoreRealm;
		this.load();
		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				saveDocument();
			}
		});
	}

	public IDocument getDocument() {
		return this.document;
	}

	public void load() {
		IDocument load;
		try {
			storage.setRealm(realm);
			load = this.storage.load();
		} catch (final IOException e) {
			this.system.handleException(e, this);
			if (this.document == null) {
				load = new BinaryRDFDocument();
				
			} else {
				load = this.document;
			}
		}
		this.document = load;
		this.document.addDataStoreListener(this.listener);
	}

	IDataStoreListener listener = new IDataStoreListener() {

		public void dataStoreChanged(IDataStore ds, ISetDelta<IEntry> entry) {
			DocumentStorage.this.markToStore();
		}

		public void entryAdding(IDataStore ds, IEntry entry) {
			DocumentStorage.this.markToStore();
		}

		public void entryRemoved(IDataStore ds, IEntry entry) {
			DocumentStorage.this.markToStore();
		}

		public void propertyChanged(IDataStore ds, IEntry e, String property,
				Object[] values) {
			DocumentStorage.this.markToStore();
		}

	};
	private boolean dirty;

	private void markToStore() {
		if (this.isStoreImmediatly()) {
			this.saveDocument();
		} else if (this.isStoreAutomatically()) {
			if (!isDirty()) {
				this.dirty = true;
				sheduleStore(syncTimeOut, this);
			}
		}
	}

	public synchronized void saveDocument() {
		if (!isDirty()) {
			return;
		}
		try {
			this.storage.store(this.document);
		} catch (final IOException e) {
			this.system.handleException(e, this);
			return;
		}
		this.dirty = false;
	}

	public boolean isStoreAutomatically() {
		return this.storeAutomatically;
	}

	public void setStoreAutomatically(boolean storeAutomatically) {
		this.storeAutomatically = storeAutomatically;
	}

	public boolean isStoreImmediatly() {
		return this.storeImmediatly;
	}

	public void setStoreImmediatly(boolean storeImmediatly) {
		this.storeImmediatly = storeImmediatly;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public ISetDelta<IEntry> storageDelta() {
		try {
			return this.storage.delta(this.document);
		} catch (final IOException e) {
			this.system.handleException(e, this);
		}
		return EmptyDelta.getDelta();
	}

	public void resolveConflicts(ISetDelta<IEntry> storageDelta) {
		try {
			this.storage.load();
		} catch (final IOException e) {
			this.system.handleException(e, this);
		}
	}

}