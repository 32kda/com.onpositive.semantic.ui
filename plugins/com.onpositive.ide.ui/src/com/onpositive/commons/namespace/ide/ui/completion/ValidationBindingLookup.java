package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.ide.ui.bindings.BindingSchemeNode;
import com.onpositive.ide.ui.bindings.BindingSchemeTree;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;

public class ValidationBindingLookup implements IExpressionEnvironment{
	
	BindingSchemeTree tree ;
	public ValidationBindingLookup( DomainEditingModelObject element, IProject project ) {
		tree = new BindingSchemeTree( element, JavaCore.create(project) ) ;
	}
	public IBinding getBinding(String path) {
		BindingSchemeNode parentScheme = tree.getParentScheme(path+'.');
		if (parentScheme==null){
			BindingSchemeNode parentScheme2 = tree.getParentScheme("this");
			if (parentScheme2.isModelExtension()){
				return new Binding(new Object());
			}
		}		
		return parentScheme != null ? new Binding(new Object()) : null ;
	}
	public IClassResolver getClassResolver() {
		return null;
	}
	
}
