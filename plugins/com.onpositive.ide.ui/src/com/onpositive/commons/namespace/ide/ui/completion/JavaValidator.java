package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;

public class JavaValidator implements ITypeValidator {

	public JavaValidator() {
		// TODO Auto-generated constructor stub
	}


	public String validate(IProject project, String value,
			DomainEditingModelObject element,String typeSpecialization) {
		IJavaProject create = JavaCore.create(project);
		try {
			IType findType = create.findType(value);
			if (findType==null){
				return "Unable to find type named '"+value+"' on build path of project '"+project.getName()+"'";
			}
			if (typeSpecialization!=null&&typeSpecialization.trim().length()>0){
				String superClassOrInterface=typeSpecialization.trim();
//				IType superClassOrInterfaceType = create.findType(superClassOrInterface);
//				if (superClassOrInterfaceType!=null){
//					return "Unable to resolve super type schema constraint for  '"+superClassOrInterface+"' on build path of project '"+project.getName()+"'";	
//				}
				ITypeHierarchy newSupertypeHierarchy = findType.newSupertypeHierarchy(new NullProgressMonitor());
				if (true){
					for (IType t:newSupertypeHierarchy.getAllSupertypes(findType)){
						if (t.getFullyQualifiedName().equals(superClassOrInterface)){
							return null;
						}
					}
					return "Only sub types of "+superClassOrInterface+" are allowed";
				}
			}
		} catch (JavaModelException e) {
			
		}
		return null;
	}
	

}
