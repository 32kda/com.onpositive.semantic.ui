package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.onpositive.commons.elements.UniversalUIElement;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public class SeparatorElement extends UniversalUIElement<Label>{

	public SeparatorElement() {
		super(Label.class, SWT.SEPARATOR);
	}
	
	@HandlesAttributeDirectly("vertical")
	public void setVertical(boolean vertical){
		if (vertical){
			style=SWT.SEPARATOR|SWT.VERTICAL;
		}
		else{
			style=SWT.SEPARATOR|SWT.HORIZONTAL;
		}
		if (isCreated()){
			recreate();
		}
	}

}
