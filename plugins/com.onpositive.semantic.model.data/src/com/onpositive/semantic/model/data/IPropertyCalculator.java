package com.onpositive.semantic.model.data;

import java.util.Set;

public interface IPropertyCalculator {

	Object getValue(Object obj, String id);

	int getValueCount(Object obj, String id);

	Set<Object> getValues(Object obj, String id);

	boolean hasValue(Object obj, Object value, String id);

}
