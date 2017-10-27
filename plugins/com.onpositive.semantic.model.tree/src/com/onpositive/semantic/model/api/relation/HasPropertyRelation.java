package com.onpositive.semantic.model.api.relation;

import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;


public class HasPropertyRelation extends PropertyRelation {

	public HasPropertyRelation(IProperty property) {
		super(property);
	}

	public boolean accept(Object element) {
		return ValueUtils.hasValue(property,element);
	}	

	public Object getPresentationObject() {
		return this.property;
	}
}
