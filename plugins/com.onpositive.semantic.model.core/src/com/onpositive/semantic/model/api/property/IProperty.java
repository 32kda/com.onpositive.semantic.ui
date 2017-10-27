package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.id.IIdentifiableObject;

/**
 * Basic property abstraction
 */
public interface IProperty extends IFunction, IIdentifiableObject {

	public final static String NAME_PROPERTY_ID = "name";

	@Override
	/**
	 * Get unique property id
	 */
	public String getId();

	@Override
	/**
	 * Get property value for given object
	 */
	Object getValue(Object obj);

	

}