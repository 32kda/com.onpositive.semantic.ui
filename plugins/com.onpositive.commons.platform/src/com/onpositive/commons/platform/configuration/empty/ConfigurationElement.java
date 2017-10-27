package com.onpositive.commons.platform.configuration.empty;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.core.runtime.IConfigurationElement;

public class ConfigurationElement implements IConfigurationElement{

	protected HashMap<String, String>attrs;
	protected ArrayList<IConfigurationElement> children=new ArrayList<IConfigurationElement>();
	protected String contributor;
	private String name;
	protected ConfigurationElement parent;
	
	public ConfigurationElement(String contributor,ConfigurationElement parent){
		this.parent=parent;
		this.contributor=contributor;
	}
	
	
	public String getAttribute(String name) {
		if (attrs!=null){
			return attrs.get(name);
		}
		return null;
	}

	public Object createExecutableExtension(String primaryObjectProperty) {
		if (attrs!=null){
			String string = attrs.get(primaryObjectProperty);
			try{
				Class<?> forName = Class.forName(string);
				return forName.newInstance();
			}catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return null;
	}

	public String getContributorId() {
		return contributor;
	}

	public IConfigurationElement[] getChildren() {		
		return children.toArray(new ConfigurationElement[children.size()]);
	}

	public String getName() {
		return name;
	}

	public void add(String localName2, String value) {
		if (attrs==null){
			attrs=new HashMap<String, String>();
		}
		attrs.put(localName2, value);
	}

	public void setName(String qName) {
		this.name=qName;
	}

}
