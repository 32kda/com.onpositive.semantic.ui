package com.onpositive.businessdroids.model;

public interface IFieldProvider {

	IField[] getFields(Object object);
	
	IField getField(String id);
	
	IFieldGroup getGroup(String id);
}
