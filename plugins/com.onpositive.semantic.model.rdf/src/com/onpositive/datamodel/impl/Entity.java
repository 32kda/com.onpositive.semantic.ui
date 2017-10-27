package com.onpositive.datamodel.impl;

import java.util.Set;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.core.ProxyEntry;
import com.onpositive.datamodel.model.DataModel;
import com.onpositive.datamodel.model.ValueClass;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;

public class Entity implements IEntry, IPropertyProvider, IType {

	public final int id;
	private int hash;
	private String url;
	public final BinaryRDFDocument owner;

	public final int id() {
		return this.id;
	}

	public final int hashCode() {
		if (this.hash != 0) {
			return this.hash;
		}
		this.hash = this.getId().hashCode();
		return this.hash;
	}

	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			if (obj instanceof IEntry) {
				return equalEntries(this, (IEntry) obj);
			}
			return false;
		}
		final Entity other = (Entity) obj;
		if (this.owner != other.owner) {
			return this.getId().equals(other.getId());
		}
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	public Entity(int id, BinaryRDFDocument owner) {
		super();
		this.id = id;
		this.owner = owner;
	}

	public final String getId() {
		if (this.url == null) {
			this.url = this.owner.getUrl(this);
		}
		return this.url;
	}

	public IRealm<IEntry> getRealm() {
		return this.owner.getRealm();
	}

	public IPropertyProvider getPropertyProvider() {
		return this;
	}

	public void addValueListener(Object obj, IValueListener<Object> listener) {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		dataStore.addValueListener(obj, listener);
	}

	public Iterable<IProperty> getProperties(Object obj) {
		return this.getDataStore().getProperties(obj);
	}

	public  IProperty getProperty(
			Object obj, String name) {
		return (IProperty) this.getDataStore()
				.getProperty(name);
	}

	private DataStoreRealm getDataStore() {
		return ((DataStoreRealm) this.owner.getRealm());
	}

	public void registerRealm(IRealm<?> realm, IValueListener<Object> listener) {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		dataStore.registerRealm(realm, listener);
	}

	public void removeValueListener(Object obj, IValueListener<Object> listener) {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		dataStore.removeValueListener(obj, listener);
	}

	public void unregisterRealm(IRealm<?> realm, IValueListener<Object> listener) {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		dataStore.unregisterRealm(realm, listener);
	}

	public Set<IType> getTypes() {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		return dataStore.getTypes(this);
	}

	public boolean isInstance(IType type) {
		final DataStoreRealm dataStore = this.getDataStore();
		if (dataStore == null) {
			throw new RuntimeException("Not Initialized"); //$NON-NLS-1$
		}
		return dataStore.isInstance(this, type);
	}

	public Set<? extends IType> getSuperClasses() {
		final ValueClass valueClass = this.getVClass();
		final Set<ValueClass> superClasses = valueClass.getSuperClasses();
		return superClasses;
	}

	private ValueClass getVClass() {
		final DataModel dataModel = this.getDataStore().getDataModel();
		final String url2 = this.getId();
		ValueClass valueClass = dataModel.getValueClass(url2);
		if (valueClass == null) {
			this.url = url2.intern();
			valueClass = dataModel.getValueClass(this.url);
		}
		return valueClass;
	}

	public Set<IProperty> getProperties() {
		final ValueClass valueClass = this.getVClass();
		return this.getDataStore().getProperties(valueClass);
	}

	public final String getName() {
		final DataStoreRealm dataStore = this.getDataStore();
		IProperty property = dataStore.getProperty("label");
		if (property != null) {
			final Object value = property.getValue(this); //$NON-NLS-1$
			if (value != null) {
				return (String) value;
			}
		}
		final ValueClass valueClass = this.getVClass();
		if (valueClass != null) {
			return valueClass.getName();
		}
		return this.getId();
	}

	public ICommandExecutor getCommandExecutor() {
		return this.getDataStore();
	}

	public static boolean equalEntries(IEntry first, IEntry obj) {

		if (first instanceof ProxyEntry) {
			final ProxyEntry pa = (ProxyEntry) first;
			first = pa.getUnderlying();
		}
		if (obj instanceof ProxyEntry) {
			final ProxyEntry pa = (ProxyEntry) obj;
			obj = pa.getUnderlying();
		}
		if ((first instanceof Entity) && (obj instanceof Entity)) {
			final Entity en = (Entity) first;
			final Entity en1 = (Entity) obj;
			if (en.owner == en1.owner) {
				return en.id == en1.id;
			}
		}
		if ((first != null) && (obj != null)) {
			final IEntry en = first;
			final IEntry en1 = obj;
			return en.getId().equals(en1.getId());
		}
		return false;
	}

	public Object getValue(String propName) {
		return this.getDataStore().getValue(this, propName);
	}

	public Set<Object> getValues(String propName) {
		return this.getDataStore().getValues(this, propName);
	}
	
	public String toString(){
		return (String) getValue("name");
	}
}