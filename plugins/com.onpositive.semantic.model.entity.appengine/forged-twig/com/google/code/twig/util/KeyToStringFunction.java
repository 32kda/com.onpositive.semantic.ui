package com.google.code.twig.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.onpositive.semantic.model.api.property.Function;

public class KeyToStringFunction extends Function<Key, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String apply(Key from)
	{
		return KeyFactory.keyToString(from);
	}
}
