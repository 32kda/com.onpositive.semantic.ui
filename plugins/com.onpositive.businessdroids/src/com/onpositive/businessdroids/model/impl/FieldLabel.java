package com.onpositive.businessdroids.model.impl;

import com.onpositive.businessdroids.model.IField;

public class FieldLabel implements IField{

	@Override
	public Object getPropertyValue(Object object) {
		IField f=(IField) object;
		return f.getTitle();
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		IEditableField fn=(IEditableField) object;
		fn.setTitle(value!=null?value.toString():"");
	}

	@Override
	public boolean isReadOnly(Object object) {
		if (object instanceof IEditableField){
			return true;
		}
		return true;
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

	@Override
	public String getId() {
		return "name";
	}

	@Override
	public String getTitle() {
		return "Name";
	}

	@Override
	public String[] getCategories() {
		return ValueOfField.STRINGS;
	}

}