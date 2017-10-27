package com.onpositive.businessdroids.model;

public interface IFieldGroup {
	String getId();
	String getTitle();
	String[] getParentGroups();
	IField[] getFields();
	IFieldGroup[] getChildGroups();
	
	int getHyerarchyDeepness();
}
