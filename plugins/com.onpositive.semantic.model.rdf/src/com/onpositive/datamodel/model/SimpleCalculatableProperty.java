package com.onpositive.datamodel.model;

import java.util.Collections;
import java.util.Set;

public abstract class SimpleCalculatableProperty implements ICalculatableProperty{

	public void setHost(CalculatablePropertyHost property) {
		
	}
	
	public abstract Object getValue(Object obj);

	public Object getValue(Object obj, String id) {
		return getValue(obj);
	}

	public int getValueCount(Object obj, String id) {
		return getValue(obj)!=null?1:0;
	}

	public Set<Object> getValues(Object obj, String id) {
		return Collections.singleton(getValue(obj, id));
	}

	public boolean hasValue(Object obj, Object value, String id) {
		Object value2 = getValue(obj);
		return value2!=null&&value2.equals(value);
	}

}
