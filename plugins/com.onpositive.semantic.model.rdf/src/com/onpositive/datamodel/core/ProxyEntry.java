package com.onpositive.datamodel.core;

import java.util.Collections;
import java.util.Set;

import com.onpositive.datamodel.impl.Entity;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;

public class ProxyEntry implements IEntry, IPropertyProvider {

	IEntry underlying;
	DataStoreRealm realm;

	public ProxyEntry(DataStoreRealm dr) {
		this.realm = dr;
	}

	public String getName() {
		if (this.underlying == null) {
			throw new IllegalStateException("Not added to realm yet"); //$NON-NLS-1$
		}
		return this.underlying.getName();
	}

	public final IRealm<IEntry> getRealm() {
		if (this.underlying != null) {
			return this.underlying.getRealm();
		}
		throw new IllegalStateException("Not added to realm yet"); //$NON-NLS-1$
	}

	public final String getId() {
		if (this.underlying != null) {
			return this.underlying.getId();
		}
		throw new IllegalStateException("Not added to realm yet"); //$NON-NLS-1$		
	}

	public final IEntry getUnderlying() {
		return this.underlying;
	}

	public final void setUnderlying(IEntry underlying) {
		this.underlying = underlying;
	}

	public final IPropertyProvider getPropertyProvider() {
		return this;
	}

	public final int hashCode() {
		if (this.underlying == null) {
			return 0;
		}
		return this.underlying.hashCode();
	}

	public boolean equals(Object obj) {
		try {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (obj instanceof IEntry) {
				return Entity.equalEntries(this, (IEntry) obj);
			}
			return false;
		} catch (final ClassCastException e) {
			return false;
		}
	}

	public Set<IType> getTypes() {
		if (this.underlying == null) {
			return Collections.emptySet();
		}
		return this.underlying.getTypes();
	}

	public boolean isInstance(IType type) {
		if (this.underlying == null) {
			return false;
		}
		return this.underlying.isInstance(type);
	}

	public void addValueListener(Object obj, IValueListener<Object> listener) {
		this.realm.addValueListener(obj, listener);
	}

	public Iterable<IProperty> getProperties(Object obj) {
		return this.realm.getProperties(obj);
	}

	public  IProperty getProperty(
			Object obj, String name) {
		return  this.realm
				.getProperty(name);
	}

	public void registerRealm(IRealm<?> realm, IValueListener<Object> listener) {
		this.realm.registerRealm(realm, listener);
	}

	public void removeValueListener(Object obj, IValueListener<Object> listener) {
		this.realm.removeValueListener(obj, listener);
	}

	public void unregisterRealm(IRealm<?> realm, IValueListener<Object> listener) {
		this.realm.unregisterRealm(realm, listener);
	}

	public ICommandExecutor getCommandExecutor() {
		return this.realm;
	}

	public Object getValue(String propName) {
		if (this.underlying != null) {
			return this.underlying.getValue(propName);
		}
		return null;
	}

	public Set<Object> getValues(String propName) {
		if (this.underlying != null) {
			return this.underlying.getValues(propName);
		}
		return Collections.emptySet();
	}
	
	public String toString(){
		return (String) getValue("name");
	}

}