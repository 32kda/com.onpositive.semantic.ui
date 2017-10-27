package com.onpositive.semantic.realm.registries;



import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.realm.IColumnDefinition;

public class ColumnDefinitionObject extends NamedEntity implements
		IColumnDefinition {

	public ColumnDefinitionObject(IConfigurationElement element) {
		super(element);
	}

	
	public boolean isEditable() {
		return getBooleanAttribute("editable", true);
	}

	
	public String propertyId() {
		return getStringAttribute("propertyId", null);
	}

	
	public boolean resizable() {
		return getBooleanAttribute("resizable", true);
	}
	
	
	public String viewerDefinition() {
		return getStringAttribute("targetViewer", null);
	}

	public ColumnConfiguration createColumnConfiguration() {
		ColumnConfiguration config=new ColumnConfiguration(this.getId());
		config.setResizable(this.resizable());
		config.setEditable(this.resizable());
		config.definition=this;		
		return config;		
	}

	
	public int priority() {
		return getIntegerAttribute("priority", 0);
	}


	public int expectedCharCount() {
		return getIntegerAttribute("expectedCharCount",10);
	}

	

}
