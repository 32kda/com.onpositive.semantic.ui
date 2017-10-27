/**
 *
 */
package com.google.code.twig.util;

import com.google.code.twig.Path;
import com.google.code.twig.Property;
import com.onpositive.semantic.model.api.property.Predicate;

public final class PathPrefixPredicate extends Predicate<Property>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Path prefix;

	public PathPrefixPredicate(Path prefix)
	{
		this.prefix = prefix;
	}

	public boolean apply(Property property)
	{
		return property.getPath().hasPrefix(prefix);
	}
}