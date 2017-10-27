package com.onpositive.commons.platform.configuration;

import org.eclipse.core.runtime.CoreException;

import com.onpositive.core.runtime.IConfigurationElement;

public class EquinoxConfigurationElement implements IConfigurationElement{

	org.eclipse.core.runtime.IConfigurationElement element;

	public EquinoxConfigurationElement(
			org.eclipse.core.runtime.IConfigurationElement e) {
		this.element=e;
	}

	public String getAttribute(String name) {
		return element.getAttribute(name);
	}

	public Object createExecutableExtension(String primaryObjectProperty) {
		try {
			return element.createExecutableExtension(primaryObjectProperty);
		} catch (CoreException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getContributorId() {
		return element.getContributor().getName();
	}

	public IConfigurationElement[] getChildren()
	{
		org.eclipse.core.runtime.IConfigurationElement[] children = element.getChildren();
		IConfigurationElement[] ch = new IConfigurationElement[ children.length ];
		int a=0;
		
		for (org.eclipse.core.runtime.IConfigurationElement e:children){
			
			ch[a++]=new EquinoxConfigurationElement(e);
		}
		return ch;
	}

	public String getName() {
		return element.getName();
	}
}
