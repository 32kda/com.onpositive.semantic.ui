package com.onpositive.datamodel.model;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.Platform;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.property.ConfigurableRealmProperty;
import com.onpositive.semantic.model.api.property.ICommandFactory;
import com.onpositive.semantic.model.api.property.IMassPropertyProvider;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyLookup;
import com.onpositive.semantic.model.api.property.IPropertyMetaData;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.IPropertyWithRealm;
import com.onpositive.semantic.model.api.property.IRealmPropertyConfigurer;
import com.onpositive.semantic.model.api.property.adapters.IRealmDependentRealmProvider;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.ITypedRealm;
import com.onpositive.semantic.model.realm.IValidator;

public class ProxyProperty implements IProperty,
		IRealmProvider<Object>, IPropertyWithRealm<Object> {	

	ICommandExecutor executor;
	IPropertyCalculator updater;
	DataStoreRealm realm;
	DefaultModelProperty parent;
	private final String id;

	public ProxyProperty(ICommandExecutor executor,
			DefaultModelProperty parent, DataStoreRealm updater,
			IPropertyCalculator calc) {
		this(executor, parent, updater);
		this.updater = calc;
	}

	public ProxyProperty(ICommandExecutor executor,
			DefaultModelProperty parent, DataStoreRealm updater) {
		super();
		this.executor = executor;
		this.parent = parent;
		this.realm = updater;
		this.updater = realm;
		this.id = parent.getId();
	}

	public ICommandExecutor getCommandExecutor() {
		return this.executor;
	}

	public Object getValue(Object obj) {
		final Object value = this.updater.getValue(obj, this.id);
		if (value == null) {
			if (this.getMinCardinality() > 0) {
				return this.parent.getDefaultValue();
			}
		}
		
		return value;
	}

	

	Boolean isJava;

	public boolean isJava() {
		if (isJava != null) {
			return isJava;
		}
		ValueClass range = getRange();
		if (range != null && range.isJava()) {
			isJava = true;
		} else {
			isJava = false;
		}
		return isJava;
	}

	public int getValueCount(Object obj) {
		return this.updater.getValueCount(obj, this.id);
	}

	public Set<Object> getValues(Object obj) {
		final Set<Object> values = this.updater.getValues(obj, this.id);

		if (values.isEmpty()) {
			if (this.getMinCardinality() > 0) {
				Object defaultValue = this.parent.getDefaultValue();
				if (defaultValue != null) {
					return Collections.singleton(defaultValue);
				}
			}
		}
		
		return values;
	}

	public boolean hasValue(Object obj, Object value) {
		final boolean hasValue = this.updater.hasValue(obj, value, this.id);
		if (!hasValue) {
			if (this.getMinCardinality() == 1) {
				if (this.getValueCount(obj) == 0) {
					if (value != null) {
						return value.equals(this.parent.getDefaultValue());
					}
				}
			}
		}
		return hasValue;
	}

	public boolean hasValue(Object obj) {
		return this.getValueCount(obj) != 0;
	}

	

	

	public boolean equals(Object obj) {
		if (obj instanceof ProxyProperty) {
			final ProxyProperty pe = (ProxyProperty) obj;
			return this.parent.equals(pe.parent);
		}
		return this.parent.equals(obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IRealmProvider.class) {
			return (T) this;
		}
		if (adapter == IMassPropertyProvider.class) { //XXX: There are no subclasses of IRealmPropertyConfigurer at all. Seems, that this code wouldn't work as expected
			final IRealmPropertyConfigurer configurer = this.parent 
					.getAdapter(IRealmPropertyConfigurer.class);
			if (configurer != null) {
				return (T) new IMassPropertyProvider() {

					public IProperty getProperty() {
						return new ConfigurableRealmProperty(
								ProxyProperty.this,
								(IPropertyLookup) ProxyProperty.this
										.getPropertyProvider(), configurer);
					}

				};
			} else {
				return null;
			}
		}
		return this.parent.getAdapter(adapter);
	}

	public String getDescription() {
		return this.parent.getDescription();
	}

	public String getId() {
		return this.parent.getId();
	}

	public int getMaxCardinality() {
		return this.parent.getMaxCardinality();
	}

	public int getMinCardinality() {
		return this.parent.getMinCardinality();
	}

	public String getName() {
		return this.parent.getName();
	}

	public DataModel getOwner() {
		return this.parent.getOwner();
	}

	public ValueClass getRange() {
		return this.parent.getRange();
	}

	public String getStringDomain() {
		return this.parent.getStringDomain();
	}

	public String getStringRange() {
		return this.parent.getStringRange();
	}

	public Class<Object> getSubjectClass() {
		return this.parent.getSubjectClass();
	}

	public int hashCode() {
		return this.parent.hashCode();
	}

	public boolean isReadOnly() {
		return this.parent.isReadOnly();
	}

	public <T> void registerAdapter(Class<T> adClass, T adapter) {
		this.parent.registerAdapter(adClass, adapter);
	}

	public void setDescription(String description) {
		this.parent.setDescription(description);
	}

	public void setDomain(String attribute) {
		this.parent.setDomain(attribute);
	}

	public void setDomain(ValueClass valueClass) {
		this.parent.setDomain(valueClass);
	}

	public void setId(String id) {
		this.parent.setId(id);
	}

	public void setMaxCardinality(int maxCardinality) {
		this.parent.setMaxCardinality(maxCardinality);
	}

	public void setMinCardinality(int minCardinality) {
		this.parent.setMinCardinality(minCardinality);
	}

	public void setName(String name) {
		this.parent.setName(name);
	}

	public void setOwner(DataModel dataModel) {
		this.parent.setOwner(dataModel);
	}

	public void setRange(String attribute) {
		this.parent.setRange(attribute);
	}

	public void setRange(ValueClass range) {
		this.parent.setRange(range);
	}

	public void setReadOnly(boolean readOnly) {
		this.parent.setReadOnly(readOnly);
	}

	public void setSubjectClass(Class<Object> subjectClass) {
		this.parent.setSubjectClass(subjectClass);
	}

	public String externalizeString(String value) {
		return this.parent.externalizeString(value);
	}

	public IValidator<?> getValidator(Object object) {
		return this.parent.getValidator(object);
	}

	public boolean isStatic() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public IRealm<Object> getRealm(IBinding model) {
		final ValueClass vClass = this.parent.getRange();
		if (vClass == null) {
			System.err.println("class " + this.parent.getStringRange()
					+ " is undefined in this data model");
			return null;
		}
		if (vClass.isObjectClass()) {
			return (IRealm) this.realm.getTypeRealm(vClass);
		}
		IRealmProvider<Object> bz = vClass.getAdapter(IRealmProvider.class);
		if (bz instanceof IRealmDependentRealmProvider) {
			final IRealmDependentRealmProvider da = (IRealmDependentRealmProvider) bz;
			return da.getRealm(model, this.realm);
		}
		if (bz != null) {
			return bz.getRealm(model);
		}
		final Class<Object> ma = vClass.getSubjectClass();
		if (ma != null) {
			bz = (IRealmProvider<Object>) Platform.getAdapterManager()
					.getAdapter(ma, IRealmProvider.class);
			if (bz != null) {
				return bz.getRealm(model);
			}
		}
		return null;
	}

	public IPropertyProvider getPropertyProvider() {
		return (IPropertyProvider) this.realm
				.getPropertyProvider();
	}

	public IRealm<? extends Object> getDomainRealm() {
		ValueClass domain = parent.getDomain();
		ITypedRealm<IEntry> typeRealm = realm.getTypeRealm(domain);
		return typeRealm;
	}

	public boolean isCalculatable() {
		return parent.getCalculator() != null;
	}

	public ICommandFactory getCommandFactory() {
		return parent;
	}

	public IPropertyMetaData getPropertyMetaData() {
		return parent;
	}
}