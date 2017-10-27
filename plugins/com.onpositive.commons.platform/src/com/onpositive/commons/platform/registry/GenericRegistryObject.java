package com.onpositive.commons.platform.registry;

import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;


/**
 * 
 * @author kor
 * 
 */
public class GenericRegistryObject implements IAdaptable2,
		Comparable<GenericRegistryObject> {

	private static final String CLASS = "class"; //$NON-NLS-1$

	private static final String ID = "id"; //$NON-NLS-1$

	private static final String NAME = "name"; //$NON-NLS-1$

	private static final String DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String ICON_URL = "icon"; //$NON-NLS-1$
	
	protected final IConfigurationElement fElement;

	private Object value;

	/**
	 * 
	 * @param element
	 */
	public GenericRegistryObject(final IConfigurationElement element) {
		super();
		this.fElement = element;
	}

	/**
	 * 
	 * @param name
	 * @return boolean value of attribute
	 */
	protected final boolean getBooleanAttribute(String name,
			boolean defaultValue) {
		final String attribute = this.fElement.getAttribute(name);
		if (attribute == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(attribute);
	}

	/**
	 * 
	 * @param name
	 * @return boolean value of attribute
	 */
	public final String getStringAttribute(String name, String defaultValue) {
		final String attribute = this.fElement.getAttribute(name);
		if (attribute == null) {
			return defaultValue;
		}
		return attribute;
	}

	/**
	 * 
	 * @param name
	 * @return boolean value of attribute
	 */
	public final int getIntegerAttribute(String name, int defaultValue) {
		final String attribute = this.fElement.getAttribute(name);
		if (attribute == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(attribute);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param name
	 * @return boolean value of attribute
	 */
	public final double getDoubleAttribute(String name, double defaultValue) {
		final String attribute = this.fElement.getAttribute(name);
		if (attribute == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(attribute);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public String getId() {
		return this.getStringAttribute(ID, System.identityHashCode(this)+"");
	}

	public String getName() {
		return this.getStringAttribute(NAME, "");
	}

	public String getDescription() {
		return this.getStringAttribute(DESCRIPTION, null);
	}

	public IResourceLink getResourceAttribute(String name) {
		final String stringAttribute = this.getStringAttribute(name, null);
		if (stringAttribute == null) {
			return null;
		}
		final String name2 = this.fElement.getContributorId();
		final IResourceLink entry = Platform.getBundle(name2).getEntry(stringAttribute);
		return entry;
	}

	public Bundle getBundle() {
		final String name2 = this.fElement.getContributorId();
		return Platform.getBundle(name2);
	}

	public <T> T getObjectAttribute(String name, Class<T> expectedClass)
			throws CoreException {
		final Object createExecutableExtension = this.fElement
				.createExecutableExtension(name);

		return expectedClass.cast(createExecutableExtension);
	}

	protected <T> T getPrimary(Class<T> expectedClass) throws CoreException {
		final Object createExecutableExtension = this.getObject();
		return expectedClass.cast(createExecutableExtension);
	}

	public Object getObject() throws CoreException {
		if (this.value != null) {
			return this.value;
		}
		final Object executableExtension = this.fElement.createExecutableExtension(this.getPrimaryObjectProperty());
		if (this.cachePrimary()) {
			this.value = executableExtension;
		}
		return executableExtension;
	}

	public IResourceLink getObjectImage() {
		return this.getResourceAttribute(ICON_URL);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(Class adapter) {
		final Object adapter2 = Platform.getAdapter(this, adapter);
		if (adapter2 != null) {
			return adapter2;
		}
		try {
			final Object object = this.getObject();
			return Platform.getAdapter(object, adapter);
		} catch (final CoreException e) {
			throw new IllegalStateException();
		}
	}

	protected String getPrimaryObjectProperty() {
		return CLASS;
	}

	protected boolean cachePrimary() {
		return true;
	}

	public int compareTo( GenericRegistryObject o ) {
		return this.getName().compareTo(o.getName());
	}

	public String toString(){
		return getId();
	}
}
