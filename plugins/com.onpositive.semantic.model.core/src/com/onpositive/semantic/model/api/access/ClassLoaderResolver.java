package com.onpositive.semantic.model.api.access;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class ClassLoaderResolver implements IClassResolver {
	
	private static final long serialVersionUID = 1L;

	protected ClassLoader classLoader ;
	
	public ClassLoaderResolver( ClassLoader classLoader ){
		this.classLoader = classLoader ;
	}

	@Override
	public Class<?> resolveClass(String className) {
		try {
			return classLoader.loadClass( className ) ;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null ;
		}
	}

	@Override
	public URL resolveResource(String className) {
		return classLoader.getResource(className);
	}
	
	@Override
	public InputStream openResourceStream(String path) throws IOException {
		return classLoader.getResource(path).openStream();
	}

}
