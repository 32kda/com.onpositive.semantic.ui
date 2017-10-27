package com.onpositive.semantic.model.api.relation;


import com.onpositive.commons.platform.registry.IAdaptable2;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.realm.AbstractFilter;

public abstract class PropertyRelation extends AbstractFilter implements
		IRelation, IAdaptable2{

	protected final IProperty property;

	public IProperty getProperty() {
		return property;
	}

	public PropertyRelation(IProperty property2) {
		this.property = (IProperty) property2;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.property == null) ? 0 : this.property.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return DefaultMetaKeys.getService(property, adapter);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PropertyRelation other = (PropertyRelation) obj;
		if (this.property == null) {
			if (other.property != null) {
				return false;
			}
		} else if (!this.property.equals(other.property)) {
			return false;
		}
		return true;
	}
}