package com.onpositive.businessdroids.model.impl.pojo;

import java.lang.reflect.Method;

import com.onpositive.businessdroids.model.IField;

public class POJOGetSet implements IField {

	protected Method getter;
	protected Method setter;
	
	public POJOGetSet(Method getter, Method setter, String id) {
		super();
		this.getter = getter;
		this.setter = setter;
		this.id = id;
	}

	private String id;
	private String[] categories;

	@Override
	public Object getPropertyValue(Object object) {
		try {
			if (object==null){
				return null;
			}
			return getter.invoke(object);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		try {
			setter.invoke(object, value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isReadOnly(Object object) {
		return setter == null;
	}

	@Override
	public Class<?> getType() {
		Class<?> returnType = getter.getReturnType();
		return POJOFactory.mapType(returnType);
	}

	

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return getId();
	}

	@Override
	public String[] getCategories() {
		return categories;
	}

}
