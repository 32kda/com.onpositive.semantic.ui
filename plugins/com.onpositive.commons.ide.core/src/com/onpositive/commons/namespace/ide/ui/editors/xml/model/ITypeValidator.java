package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import org.eclipse.core.resources.IProject;

/**
 * Used for validating attribute values
 * @author kor & 32kda
 *
 */
public interface ITypeValidator 
{
	/**
	 * Validate, is value compatible with current type or not 
	 * @param value Value to ceck
	 * @param string 
	 * @param domainEditingModelObject 
	 * @return null if all OK, error string otherwise
	 */
	String validate(IProject project,String value, DomainEditingModelObject element, String typeSpecialization);
	//public int getErrorOffset(){ return -1 ; }

}
