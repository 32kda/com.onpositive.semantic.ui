package com.onpositive.datamodel.core;

import com.onpositive.datamodel.impl.IStorage;
import com.onpositive.datamodel.impl.StorageConfiguration;

public interface IStorageProvider {

	public IStorage getStorage(StorageConfiguration configuration);

}
