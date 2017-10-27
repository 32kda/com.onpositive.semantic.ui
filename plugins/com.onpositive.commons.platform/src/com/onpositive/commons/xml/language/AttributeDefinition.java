package com.onpositive.commons.xml.language;

public class AttributeDefinition {
	public AttributeDefinition(String name, String type, boolean required) {
		this.name = name ;
		this.type = type ;
		this.required = required ;
		this.ignoreOnValidation = false ;
	}
	public AttributeDefinition(String name, String type, boolean required, boolean ignore ) {
		this.name = name ;
		this.type = type ;
		this.required = required ;
		this.ignoreOnValidation = ignore ;
	}

	String name;
	String type;
	boolean required;
	boolean ignoreOnValidation ;
}