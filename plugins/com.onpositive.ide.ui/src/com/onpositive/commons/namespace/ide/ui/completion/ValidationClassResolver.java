package com.onpositive.commons.namespace.ide.ui.completion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import com.onpositive.semantic.model.api.access.IClassResolver;

public class ValidationClassResolver implements IClassResolver {
	
	private static final long serialVersionUID = 3195218859943735144L;
	IJavaProject project ;
	
	public ValidationClassResolver( IJavaProject project ){		
		this.project = project ;
	}

	public Class<?> resolveClass(String className) {
		try {
			return project.findType( className ) != null ? int.class : null ;
			} catch (JavaModelException e) {
			e.printStackTrace();
			return null ;
		}
	}

	public URL resolveResource(String className) {
		return null; //Not supported yet
	}

	public InputStream openResourceStream(String path) throws IOException {
		return null; //Not supported yet
	}

}
