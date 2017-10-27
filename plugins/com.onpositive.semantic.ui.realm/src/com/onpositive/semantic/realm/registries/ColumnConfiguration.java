package com.onpositive.semantic.realm.registries;

import org.eclipse.swt.graphics.Image;

import com.onpositive.semantic.realm.IColumnDefinition;

public class ColumnConfiguration {

	transient ColumnDefinitionObject definition;
	
	private String columnDefinitionId;

	private boolean editable;

	public boolean isEditable() {
		return editable;
	}

	public boolean isResizable() {
		return resizable;
	}

	private boolean resizable;
	

	public ColumnConfiguration(String columnDefinitionId){
		this.columnDefinitionId=columnDefinitionId;
		getDefinition();
	}
	
	public ColumnConfiguration(){
		
	}
	
	public IColumnDefinition getDefinition(){
		if (columnDefinitionId==null){
			throw new RuntimeException();
		}
		if (definition==null){
			definition=ColumnDefinitionRegistry.getInstance().get(columnDefinitionId);
		}
		return definition;
	}

	public String getId() {
		return columnDefinitionId;
	}

	public void setEditable(boolean resizable) {
		this.editable=resizable;
	}
	
	public void setResizable(boolean resizable) {
		this.resizable=resizable;
	}
	
	public String toString(){
		getDefinition();
		return definition.name();
	}
	
	public Image getImage(){
		getDefinition();
		return definition.getImage();
	}

	public String name() {
		getDefinition();
		return definition.name();
	}
	
	public String description() {
		getDefinition();
		return definition.name();
	}
	
	public int getExpectedCharCount(){
		getDefinition();
		return Math.max(definition.expectedCharCount(),name().length()+10);
	}
}
