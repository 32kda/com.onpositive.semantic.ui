package com.onpositive.datamodel.model;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.datamodel.core.IStorageProvider;
import com.onpositive.datamodel.impl.IStorage;
import com.onpositive.datamodel.impl.StorageConfiguration;

public class StorageRegistry extends RegistryMap<GenericRegistryObject> {

	private StorageRegistry() {
		super("com.onpositive.semantic.model.rdf.storageProtocol",
				GenericRegistryObject.class);
	}

	static StorageRegistry instance;

	public static StorageRegistry getInstance() {
		if (instance == null) {
			instance = new StorageRegistry();
		}
		return instance;
	}

	public IStorage getStorage(StorageConfiguration configuration) {
		final GenericRegistryObject object = this.get(configuration
				.getProvider());
		if (object != null) {
			IStorageProvider provider;
			try {
				provider = (IStorageProvider) object.getObject();
				return provider.getStorage(configuration);
			} catch (com.onpositive.core.runtime.CoreException e) {
				throw new IllegalArgumentException(e);
			}
		
		}
		return null;
	}
}