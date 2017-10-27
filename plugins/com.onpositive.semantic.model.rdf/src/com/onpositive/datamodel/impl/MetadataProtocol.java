package com.onpositive.datamodel.impl;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import com.onpositive.datamodel.core.DataModelPlugin;
import com.onpositive.datamodel.core.IStorageProvider;

public class MetadataProtocol implements IStorageProvider {

	public MetadataProtocol() {

	}

	public IStorage getStorage(StorageConfiguration configuration) {
		final String ps = configuration.getLocalId();
		final File fs = Platform.getStateLocation(
				DataModelPlugin.getInstance().getBundle()).append(ps).toFile();
		return new FileStorage(fs);
	}
}
