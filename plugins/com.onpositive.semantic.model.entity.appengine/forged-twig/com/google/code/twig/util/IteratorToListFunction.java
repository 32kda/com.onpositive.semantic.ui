package com.google.code.twig.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.onpositive.semantic.model.api.property.Function;

public class IteratorToListFunction<T> extends Function<Iterator<T>, List<T>>
{
	private static final long serialVersionUID = 1L;

	@Override
	public List<T> apply(Iterator<T> from)
	{
		List<T> result = new ArrayList<T>();
		while (from.hasNext())
		{
			result.add(from.next());
		}
		return result;
	}
}
