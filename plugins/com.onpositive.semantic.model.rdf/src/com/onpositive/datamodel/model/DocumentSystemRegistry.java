package com.onpositive.datamodel.model;

import java.io.File;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.onpositive.datamodel.core.DataModelPlugin;
import com.onpositive.datamodel.impl.FileStoragePropertyStore;
import com.onpositive.datamodel.impl.StorageConfiguration;

public class DocumentSystemRegistry {


	private static DocumentSystemRegistry instance;

	WeakHashMap<ConfigurableDocumentSystem, DocumentSystemRegistry> cache = new WeakHashMap<ConfigurableDocumentSystem, DocumentSystemRegistry>();
	
	private DocumentSystemRegistry() {
	}

	public static synchronized DocumentSystemRegistry getInstance() {
		if (instance == null) {
			instance = new DocumentSystemRegistry();
		}
		return instance;
	}

	public static ConfigurableDocumentSystem getDocumentSystem(String id) {
		return getInstance().getSystem(id);
	}

	public synchronized ConfigurableDocumentSystem getSystem(String id) {

		for (final ConfigurableDocumentSystem s : this.cache.keySet()) {
			if (s.getId().equals(id)) {
				return s;
			}
		}
		final IConfigurationElement[] elements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						"com.onpositive.semantic.model.rdf.documentSystem"); //$NON-NLS-1$
		for (final IConfigurationElement e : elements) {
			final String atr = e.getAttribute("id"); //$NON-NLS-1$
			if (atr.equals(id)) {
				final ConfigurableDocumentSystem s = this.createSystem(atr, e);
				this.cache.put(s, this);
				return s;
			}
		}
		return null;
	}

	private ConfigurableDocumentSystem createSystem(String atr,
			IConfigurationElement e) {
		final String attribute = e.getAttribute("modelId"); //$NON-NLS-1$
		if (attribute != null) {
			final DataModel ds = ModelRegistry.getInstance()
					.getModel(attribute);
			final ConfigurableDocumentSystem ca = new ConfigurableDocumentSystem(
					atr, ds);
			System.out.println("Creating document system");
			ca.setInited(false);
			if (!ca.isInited()) {
				for (final IConfigurationElement a : e.getChildren()) {
					final String ids = a.getAttribute("id");
					String name = a.getName();
					if (name.equals("propertyBlob")) {
						final File fs = Platform.getStateLocation(
								DataModelPlugin.getInstance().getBundle()).append(attribute).append(ids).toFile();
						fs.getParentFile().mkdirs();
						FileStoragePropertyStore sto=new FileStoragePropertyStore(ids,fs.getAbsolutePath());
						ca.addDataStore(sto);
					} else {
						final StorageConfiguration storage = new StorageConfiguration();
						storage.setName(a.getAttribute("name"));
						storage.setProvider("metadata");
						storage.setSynctimeout(60000);
						storage.setUrl(ids);
						ca.addStorage(storage);
					}
				}
			}
			ca.setInited(true);
			ca.saveConfiguration();
			return ca;
		}
		return null;
	}
}