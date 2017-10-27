package com.onpositive.businessdroids.model;

public interface IField {

	public abstract Object getPropertyValue(Object object);

	public abstract void setPropertyValue(Object object, Object value);

	public boolean isReadOnly(Object object);

	public abstract Class<?> getType();

	public abstract String getId();

	public abstract String getTitle();
	
	public String[] getCategories();

}
