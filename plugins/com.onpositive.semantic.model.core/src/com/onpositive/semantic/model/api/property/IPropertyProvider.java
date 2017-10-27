package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.meta.IService;

/**
 * 
 * Basic interface for accessing properties of the object
 * 
 * if union of known properties for all possible inputs changes
 * provider should fire change using {@link ObjectChangeManager}. 
 * 
 * @author Kor
 *
 *
 */
public interface IPropertyProvider extends IService{


	/**
	 * 
	 * @param obj - may be null
	 * @param name
	 * @return property with a given id, or null if no property with a given id exists
	 * for a given object
	 */
	public abstract IProperty getProperty(
			Object obj, String name);


	//TODO think about scoping in future
	/**
	 * Returns collection of known properties for a given object
	 * system properties are not returned inside of this collection
	 * 
	 * it object is null returns all known properties
	 */
	Iterable<IProperty> getProperties(Object obj);
}