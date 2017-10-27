package com.onpositive.semantic.realm;

public interface IColumnDefinition extends INamedEntity{

	boolean resizable();

	String  propertyId();

	String  viewerDefinition();
	
	boolean isEditable();

	int priority();
}
