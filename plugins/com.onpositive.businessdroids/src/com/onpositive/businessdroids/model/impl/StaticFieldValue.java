package com.onpositive.businessdroids.model.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.onpositive.businessdroids.model.IField;

public class StaticFieldValue implements IField {

	@Override
	public Object getPropertyValue(Object object) {
		Field field = (Field) object;
		if ((field.getModifiers() & Modifier.STATIC) == 0)
			throw new IllegalArgumentException("Can't use for non-static fields!");
		try {
			return field.get(null).toString();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		throw new UnsupportedOperationException("Value modification is not supported yet.");	
	}

	@Override
	public boolean isReadOnly(Object object) {
		return true;
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

	@Override
	public String getId() {
		return "Value";
	}

	@Override
	public String getTitle() {
		return getId();
	}

	@Override
	public String[] getCategories() {
		return null;
	}

}
