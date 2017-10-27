package com.onpositive.semantic.model.data;

import java.util.HashMap;


import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.property.ICommandFactory;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyMetaData;
import com.onpositive.semantic.model.api.property.adapters.ILabelLookup;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.property.java.annotations.Unique;
import com.onpositive.semantic.model.realm.IValidator;

public class DefaultModelProperty implements ICommandFactory, IPropertyMetaData {

	private String name;
	private String id;
	private String description;
	private int maxCardinality;
	private int minCardinality;
	private boolean readOnly;
	private boolean transitive;
	
	private Object defaultValue;
	private String defaultValueString;

	private ValueClass range;

	private String stringRange;

	private String stringDomain;

	private ValueClass domain;

	private DataModel owner;
	
	private ICalculatableProperty calculator;
	
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public <T> T getAdapter(Class<T> adapter) {
		if (this.adapters.containsKey(adapter)) {
			//final T cast = adapter.cast();
			return (T) this.adapters.get(adapter);
		}
		if (this.range != null) {
			return this.range.getAdapter(adapter);
		}
		return null;
	}

	
	private Class<Object> subjectClass = Object.class;
	private final HashMap<Class<?>, Object> adapters = new HashMap<Class<?>, Object>();
	private boolean searchable;

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public Class<Object> getSubjectClass() {
		if (this.range != null) {
			return this.range.getSubjectClass();
		}
		return this.subjectClass;
	}


	public ICommand createRemovePropertyCommand(Object type) {
		return new SimpleOneArgCommand(type, null,
				SimpleOneArgCommand.REMOVE_ALL_VALUES, this);
	}

	public ICommand createRemoveValueCommand(IProperty property, Object type, Object value) {
		return new SimpleOneArgCommand(type, value,
				SimpleOneArgCommand.REMOVE_VALUE, this);
	}

	public ICommand createAddValueCommand(IProperty property, Object type, Object value) {
		return new SimpleOneArgCommand(type, value,
				SimpleOneArgCommand.ADD_VALUE, this);
	}

	public ICommand createSetValueCommand(IProperty property, Object type, Object value) {
		return new SimpleOneArgCommand(type, value,
				SimpleOneArgCommand.SET_VALUE, this);
	}

	public ICommand createSetValuesCommand(IProperty property, Object type, Object... values) {
		return new SimpleOneArgCommand(type, values,
				SimpleOneArgCommand.SET_VALUES, this);
	}

	public DefaultModelProperty(String id) {
		this.id = id.intern();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id.intern();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getDescription() {
		return this.description;
	}

	public String getId() {
		return this.id;
	}

	public int getMaxCardinality() {
		return this.maxCardinality;
	}

	public int getMinCardinality() {
		return this.minCardinality;
	}

	public String getName() {
		return this.name;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public ValueClass getRange() {
		return this.range;
	}

	public void setRange(ValueClass range) {
		this.range = range;
	}

	public void setSubjectClass(Class<Object> subjectClass) {
		this.subjectClass = subjectClass;
	}

	public <T> void registerAdapter(Class<T> adClass, T adapter) {
		this.adapters.put(adClass, adapter);
	}

	public void setRange(String attribute) {
		this.stringRange = attribute.intern();
	}

	public void setDomain(String attribute) {
		this.stringDomain = attribute.intern();
	}

	public String getStringRange() {
		if (this.range != null) {
			return this.range.getId();
		}
		return this.stringRange;
	}

	public String getStringDomain() {
		if (this.domain != null) {
			return this.domain.getId();
		}
		return this.stringDomain;
	}

	public void setDomain(ValueClass valueClass) {
		valueClass.addProperty(this);
		this.domain = valueClass;
	}

	public void setOwner(DataModel dataModel) {
		this.owner = dataModel;
	}

	public DataModel getOwner() {
		return this.owner;
	}

	public String externalizeString(String value) {
		return value;
	}

	public IValidator<?> getValidator(Object object) {
		if (this.range != null) {
			return this.range.getValidator();
		}
		return null;
	}

	public boolean isTransitive() {
		return this.transitive;
	}

	public void setTransitive(boolean transitive) {
		this.transitive = transitive;
	}

	public ValueClass getDomain() {
		return this.domain;
	}

	public String getDefaultValueString() {
		return this.defaultValueString;
	}

	public Object getDefaultValue() {
		if (this.defaultValue != null) {
			return this.defaultValue;
		}
		if (this.defaultValueString != null) {
			final ILabelLookup adapter = this.getAdapter(ILabelLookup.class);
			if (adapter != null) {
				try {
					this.defaultValue = adapter.lookUpByLabel(null,
							this.defaultValueString);
				} catch (final NotFoundException e) {
					Activator.log(e);
				}
			}
		}
		return this.defaultValue;
	}

	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}

	public void setUnique(boolean parseBoolean2) {
		if (parseBoolean2) {
			adapters.put(Unique.class, this);
		} else {
			adapters.remove(Unique.class);
		}
	}

	public void setCalculator(ICalculatableProperty newInstance) {
		this.calculator=newInstance;
	}

	public ICalculatableProperty getCalculator() {
		return calculator;
	}
	
	public boolean hasDescription() {
		return true;
	}

	public boolean hasName() {
		return true;
	}

	public boolean isMultivalue() {
		return maxCardinality>1;
	}

	public boolean isRequired() {
		return minCardinality>0;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public boolean isIndexable() {
		return false;
	}

	public boolean isFixedBound() {
		return false;
	}
}