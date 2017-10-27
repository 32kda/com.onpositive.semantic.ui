package com.onpositive.ide.ui;

import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.IProject;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;

public class RegexpValidator implements ITypeValidator {

	public String validate(IProject project, String value,
			DomainEditingModelObject element,String typeSpecialization) {
		try{
		PatternConstructor.createPattern(value, true, true);
		}catch (PatternSyntaxException e) {
			return e.getMessage();
		}
		return null;
	}

}
