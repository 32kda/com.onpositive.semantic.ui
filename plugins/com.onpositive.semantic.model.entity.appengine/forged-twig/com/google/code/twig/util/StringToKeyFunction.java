package com.google.code.twig.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.onpositive.semantic.model.api.property.Function;;

public class StringToKeyFunction extends Function<String, Key>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Key apply(String from)
	{
		return KeyFactory.stringToKey(from);
	}
}
