package com.onpositive.commons.platform.configuration;

import java.io.Serializable;

/**
 * @author Pavel Petrochenko represents artifact configuration
 */
public interface IAbstractConfiguration extends Serializable {
	/**
	 * @param name
	 * @return value of attribute or -1 if attribute is not defined
	 */
	int getIntAttribute(String name);

	/**
	 * @param name
	 * @return value of attribute or empty string if attribute is not defined
	 */
	String getStringAttribute(String name);

	/**
	 * @param name
	 * @return value of attribute or false if attribute is not defined
	 */
	boolean getBooleanAttribute(String name);

	/**
	 * @param name
	 * @return value of attribute or empty array if attribute is not defined
	 */
	String[] getStringArrayAttribute(String name);

	/**
	 * @param name
	 * @param value
	 */
	void setIntAttribute(String name, int value);

	/**
	 * @param name
	 * @param value
	 */
	void setBooleanAttribute(String name, boolean value);

	/**
	 * @param name
	 * @param value
	 */
	void setStringAttribute(String name, String value);

	/**
	 * @param name
	 * @param value
	 *            strings should not contain commas
	 */
	void setStringArrayAttribute(String name, String[] value);

	/**
	 * @param name
	 */
	void removeAttribute(String name);

	/**
	 * @param name
	 * @return
	 */
	IAbstractConfiguration getSubConfiguration(String name);

	/**
	 * @param name
	 * @return new instance of configuration instance initially is no
	 */
	IAbstractConfiguration createSubConfiguration(String name);

	/**
	 * @param name
	 * @param configuration
	 */
	void setSubConfiguration(String name, IAbstractConfiguration configuration);

	/**
	 * @return names of known properties
	 */
	String[] propertyNames();

	/**
	 * 
	 * @param name
	 * @param value
	 */
	void setLongAttribute(String name, long value);

	/**
	 * 
	 * @param name
	 * @param value
	 */
	void setDoubleAttribute(String name, double value);

	/**
	 * 
	 * @param name
	 * @return
	 */
	long getLongAttribute(String name);

	/**
	 * 
	 * @param name
	 * @return
	 */
	double getDoubleAttribute(String name);

	void flush();
}
