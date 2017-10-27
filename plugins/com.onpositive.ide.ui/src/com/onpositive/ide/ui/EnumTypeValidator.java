package com.onpositive.ide.ui;

import org.eclipse.core.resources.IProject;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;

public class EnumTypeValidator implements ITypeValidator {

	public EnumTypeValidator() {
	}

	public String validate(IProject project, String value,
			DomainEditingModelObject element, String typeSpecialization) {
		if (typeSpecialization!=null&&typeSpecialization.length()>0){
			String[] split = typeSpecialization.split(",");
			for (String s:split){
				if (value.equals(s.trim())){
					return null;
				}
			}
			return "Only following values are allowed:"+typeSpecialization;
		}
		return null;
	}

}
