package com.onpositive.businessdroids.model.impl;

import com.onpositive.businessdroids.model.IField;

public abstract class AbstractComputableField implements IField{

	public AbstractComputableField(Class<?> type, String title, String id) {
		super();
		this.type = type;
		this.title = title;
		this.id = id;
	}

	public AbstractComputableField() {
		this(Object.class,"","");
	}

	protected Class<?>type;
	protected String title;
	protected String id;
	
	

	@Override
	public void setPropertyValue(Object object, Object value) {
		
	}

	@Override
	public boolean isReadOnly(Object object) {
		return true;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String[] getCategories() {
		return new String[0];
	}

}
