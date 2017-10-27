package com.onpositive.semantic.model.api.property;

public interface IPropertyWithHasValue extends IProperty{

	boolean hasValue(Object obj);

	boolean hasValue(Object obj, Object value);
}
