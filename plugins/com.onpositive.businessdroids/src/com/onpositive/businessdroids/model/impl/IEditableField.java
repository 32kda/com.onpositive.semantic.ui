package com.onpositive.businessdroids.model.impl;

import com.onpositive.businessdroids.model.IField;

public interface IEditableField extends IField {

	public abstract void setType(Class<?> type);

	public abstract void setId(String id);

	public abstract void setTitle(String title);

}
