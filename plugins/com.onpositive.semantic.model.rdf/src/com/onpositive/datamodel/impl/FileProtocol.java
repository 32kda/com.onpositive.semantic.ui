package com.onpositive.datamodel.impl;

import java.io.File;

import com.onpositive.datamodel.core.IStorageProvider;

public class FileProtocol implements IStorageProvider {

	public FileProtocol() {

	}

	public IStorage getStorage(StorageConfiguration configuration) {
		final String ps = configuration.getLocalId();
		final File fs = new File(ps);
		return new FileStorage(fs);
	}
}
