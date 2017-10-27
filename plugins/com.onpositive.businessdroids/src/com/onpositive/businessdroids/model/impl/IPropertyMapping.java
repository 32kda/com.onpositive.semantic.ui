package com.onpositive.businessdroids.model.impl;

import com.onpositive.businessdroids.model.IField;

public interface IPropertyMapping {

	public Object getPropertyValue(Object object, IField field);

	public void setPropertyValue(Object object, IField field, Object value);

}
