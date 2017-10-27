package com.onpositive.semantic.model.ui.property.editors;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.EditableListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.viewer.structured.TreeContentProvider;

public class TreeElement extends EditableListEnumeratedValueSelector{

	public TreeElement(){
		setAsTree(true);
	}
	
	@HandlesAttributeDirectly("property")
	public void setProperty(String property){
		TreeContentProvider provider = new TreeContentProvider();
		provider.setProperty(property);
		setContentProvider(provider);
	}
}
