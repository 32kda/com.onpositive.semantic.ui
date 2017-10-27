package com.onpositive.businessdroids.model.impl;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.businessdroids.model.IField;


public class BasicDataMapping implements IPropertyMapping {

	@Override
	public Object getPropertyValue(Object object, IField field) {
		if (object instanceof HashMap) {
			return ((Map<?, ?>) object).get(field.getId());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyValue(Object object, IField field, Object value) {
		if (object instanceof HashMap) {
			((Map<String, Object>) object).put(field.getId(), value);
		}
	}

}
