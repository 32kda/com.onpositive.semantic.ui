package com.google.code.twig.util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.onpositive.semantic.model.api.property.Function;

public class EntityToKeyFunction extends Function<Entity, Key>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Key apply(Entity arg0)
	{
		return arg0.getKey();
	}	

	@Override
	public Object getValue(Object arg0) {
		return apply((Entity) arg0);
	}
}