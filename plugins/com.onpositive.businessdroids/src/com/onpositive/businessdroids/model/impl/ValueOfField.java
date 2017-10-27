package com.onpositive.businessdroids.model.impl;

import com.onpositive.businessdroids.model.IField;

public class ValueOfField implements IField {

	static final String[] STRINGS = new String[0];
	protected Object object;
	
	public ValueOfField(Object object, String id) {
		super();
		this.object = object;
		this.id = id;
	}

	protected String id;

	@Override
	public Object getPropertyValue(Object object) {
		IField f=(IField) object;
		return f.getPropertyValue(this.object);
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		IField f=(IField) object;
		f.setPropertyValue(this.object, value);
	}

	@Override
	public boolean isReadOnly(Object object) {
		IField f=(IField) object;
		return f.isReadOnly(this.object);
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return id;
	}

	@Override
	public String[] getCategories() {
		return STRINGS;
	}
}