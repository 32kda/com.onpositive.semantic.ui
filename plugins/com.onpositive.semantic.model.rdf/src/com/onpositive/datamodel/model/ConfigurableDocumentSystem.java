package com.onpositive.datamodel.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.commons.platform.configuration.ConfigurationPersistence;
import com.onpositive.commons.platform.configuration.EclipsePreferencesConfiguration;
import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.DocumentSystem;
import com.onpositive.datamodel.impl.DocumentSystemConfiguration;
import com.onpositive.datamodel.impl.FileStoragePropertyStore;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.datamodel.impl.IStorage;
import com.onpositive.datamodel.impl.StorageConfiguration;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.undo.UndoRedoSupportExtension;

public class ConfigurableDocumentSystem {

	private static EclipsePreferencesConfiguration pluginPreferences = EclipsePreferencesConfiguration
			.getPluginPreferences("documentSystem");

	private final HashMap<StorageConfiguration, IStorage> storageMap = new HashMap<StorageConfiguration, IStorage>();

	private final String id;
	private final DocumentSystem system;
	private final DocumentSystemConfiguration configuration;

	public ExecutableCommand newTransaction(boolean undoable) {
		return new ExecutableCommand((DataStoreRealm) this.getRealm(), undoable);
	}

	public ExecutableCommand newSetValue(IProperty property,
			List<?> target, Object value, boolean undoable) {
		ExecutableCommand newTransaction = newTransaction(undoable);
		for (Object e : target) {
			if (e instanceof IEntry) {
				IEntry m = (IEntry) e;
				ICommand createSetValueCommand = property.getCommandFactory()
						.createSetValueCommand(property, m, value);
				newTransaction.addCommand(createSetValueCommand);
			}
		}
		return newTransaction;
	}

	public <T> ProxyRealm<T> newTypedProxyRealm(Class<T> clazz) {
		return new ProxyRealm<T>(clazz, getRealm());
	}

	public ConfigurableDocumentSystem(String atr, DataModel ds) {
		this.id = atr;
		this.system = new DocumentSystem(ds);
		this.system.getRealm().setOwner(this);
		final IAbstractConfiguration configuration = pluginPreferences
				.getSubConfiguration(atr);
		this.configuration = ConfigurationPersistence.load(
				DocumentSystemConfiguration.class, configuration);
		this.system.getRealm().setChangeManager(
				UndoRedoSupportExtension.getUndoRedoChangeManager());
		/*for (final StorageConfiguration s : storages) {
			final IStorage localStorage = StorageRegistry.getInstance()
					.getStorage(s);
			if (localStorage == null) {
				throw new RuntimeException();
			}
			try {
				this.system.addStorage(localStorage);
			} catch (final IOException e) {
				throw new RuntimeException();
			}
			this.storageMap.put(s, localStorage);
		}*/
	}

	public void saveConfiguration() {
		ConfigurationPersistence.store(this.configuration, pluginPreferences
				.getSubConfiguration(this.id));
		pluginPreferences.flush();
	}

	public IDataStoreRealm getRealm() {
		return this.system.getRealm();
	}

	public ArrayList<StorageConfiguration> getStorages() {
		return new ArrayList<StorageConfiguration>(this.configuration
				.getStorages());
	}

	public String getName() {
		return this.configuration.getName();
	}

	void setName(String name) {
		this.configuration.setName(name);
	}

	public String getId() {
		return this.id;
	}

	public boolean isInited() {
		return this.configuration.isInited();
	}

	void setInited(boolean b) {
		this.configuration.setInited(b);
	}

	public void addStorage(StorageConfiguration storage) {
		final IStorage localStorage = StorageRegistry.getInstance().getStorage(
				storage);
		if (localStorage == null) {
			throw new RuntimeException();
		}
		try {
			this.system.addStorage(localStorage);
		} catch (final IOException e) {
			throw new RuntimeException();
		}
		this.storageMap.put(storage, localStorage);
		this.configuration.addStorage(storage);
		
	}

	public void removeStorage(StorageConfiguration storage) {
		final IStorage ds = this.storageMap.remove(storage);
		if (ds != null) {
			this.system.removeStorage(ds);
			this.configuration.removeStorage(storage);
			this.saveConfiguration();
		}
	}

	public void updateStorage(StorageConfiguration configuration) {
		this.removeStorage(configuration);
		this.addStorage(configuration);
	}

	public void addDataStore(FileStoragePropertyStore sto) {
		this.system.addDataStore(sto);
	}
}