package com.onpositive.core.runtime;

public interface IConfigurationElement {

	public String getAttribute(String name);

	public Object createExecutableExtension(String primaryObjectProperty);

	public String getContributorId();

	public IConfigurationElement[] getChildren();

	public String getName();
}
