package com.onpositive.semantic.ui.android;

import com.onpositive.businessdroids.model.IField;

public class ToStringField implements IField {

	@Override
	public Object getPropertyValue(Object object) {
		return object.toString();
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		// Not supported
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
		return "value";
	}

	@Override
	public String getTitle() {
		return "Value";
	}

	@Override
	public String[] getCategories() {
		return new String[0];
	}

}
