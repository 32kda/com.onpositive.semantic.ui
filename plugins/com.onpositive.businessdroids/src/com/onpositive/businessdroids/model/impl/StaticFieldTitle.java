package com.onpositive.businessdroids.model.impl;

import java.lang.reflect.Field;

import com.onpositive.businessdroids.model.IField;

public class StaticFieldTitle implements IField {

	@Override
	public Object getPropertyValue(Object object) {
		String name = ((Field)object).getName();
		name = name.toLowerCase();
		char letter = Character.toUpperCase(name.charAt(0));
		name = letter + name.substring(1);
		return name;
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		throw new UnsupportedOperationException("Can't modify field name!");
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
		return "Title";
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
