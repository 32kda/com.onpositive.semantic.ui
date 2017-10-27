package com.onpositive.core.runtime;

import java.io.InputStream;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.IResourceLink;

public interface Bundle {

	public Class<?> loadClass(String className) throws ClassNotFoundException;

	public IResourceLink getEntry(String stringAttribute);
	
	public IAbstractConfiguration getPreferences();	

	public String getSymbolicName();

	public InputStream getResourceAsStream(String attribute);

}
