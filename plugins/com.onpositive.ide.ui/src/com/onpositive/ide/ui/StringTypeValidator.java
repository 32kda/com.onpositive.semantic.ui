package com.onpositive.ide.ui;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;

public class StringTypeValidator implements ITypeValidator {

	public StringTypeValidator() {	
	}

	public String validate(IProject project, String value,
			DomainEditingModelObject element, String typeSpecialization) {
		if (typeSpecialization!=null&&typeSpecialization.trim().length()>0){
			if (!Pattern.matches(typeSpecialization, value)){
				return "This attribute values are constrained to following regexp:"+typeSpecialization;
			}
		}
		return null;
	}

}
