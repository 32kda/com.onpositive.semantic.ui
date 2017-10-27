package com.onpositive.commons.xml.language;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.onpositive.core.runtime.Bundle;

public class ObjectReference {

	protected final Bundle bundleContext;
	protected final String className;
	protected Object object;
	protected Class<?> loadClass;

	public ObjectReference(Bundle bundleContext, String className) {
		super();
		this.bundleContext = bundleContext;
		this.className = className;
	}

	public Object getObject() {
		if (this.object != null) {
			return this.object;
		} else {
			return newInstance();
		}
		
	}

	protected Object newInstance() {
		if ( loadClass == null )
		{
			try {
				loadClass = this.bundleContext.loadClass( this.className );
				}
			catch (final ClassNotFoundException e) {
				e.printStackTrace();
				Activator.log(e);
				System.err.print("Error, cannot create instance of class " + this.className );
				return null ;
			}
		}
		if( loadClass != null )
		{
			try {
				this.object = loadClass.newInstance();
				return this.object;
			} catch (final Exception e) {
				try {
					Constructor<?> constructor = loadClass.getConstructor(Bundle.class,String.class);
					this.object=constructor.newInstance(bundleContext,className);
					return this.object;
				} catch (Exception e1) {
					Activator.log(e);
				}
				Activator.log(e);
				System.err.print("Cannot create instance of class " + loadClass.getName()+'\n' );
				return null ;
			} 
		}
		return null ;

	}

	public Class<?> load(String modelClass) {
		try {
			return bundleContext.loadClass(modelClass);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}	
}
