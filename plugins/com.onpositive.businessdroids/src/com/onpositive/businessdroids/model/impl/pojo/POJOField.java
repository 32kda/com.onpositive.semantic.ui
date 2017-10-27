package com.onpositive.businessdroids.model.impl.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.onpositive.businessdroids.model.IField;

public class POJOField implements IField{

	private Field fld;
	private String[] categories;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fld == null) ? 0 : fld.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POJOField other = (POJOField) obj;
		if (fld == null) {
			if (other.fld != null)
				return false;
		} else if (!fld.equals(other.fld))
			return false;
		return true;
	}

	public POJOField(Field field) {
		this.fld=field;
		field.setAccessible(true);
	}

	@Override
	public Object getPropertyValue(Object object) {
		try{
		if (object==null){
			return null;
		}
		return fld.get(object);
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		try{
		fld.set(object, value);
		}catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean isReadOnly(Object object) {
		return Modifier.isFinal(fld.getModifiers());
	}

	@Override
	public Class<?> getType() {
		return POJOFactory.mapType(fld.getType());
	}

	@Override
	public String getId() {
		return fld.getName();
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
