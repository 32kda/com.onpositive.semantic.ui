package com.onpositive.commons.platform.configuration;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.IExtensionRegistry;

public class EquinoxExtensionRegistry implements IExtensionRegistry{

	org.eclipse.core.runtime.IExtensionRegistry parent;

	public EquinoxExtensionRegistry(
			org.eclipse.core.runtime.IExtensionRegistry parent) {
		super();
		this.parent = parent;
	}

	public IConfigurationElement[] getConfigurationElementsFor( String extensionPoint ) 
	{
		org.eclipse.core.runtime.IConfigurationElement[] children = parent.getConfigurationElementsFor( extensionPoint );
		IConfigurationElement[] ch=new IConfigurationElement[ children.length ];
		
		int a=0;
		for ( org.eclipse.core.runtime.IConfigurationElement e : children ){
			ch[ a++ ]=new EquinoxConfigurationElement( e );
		}
		return ch;
	}
}
