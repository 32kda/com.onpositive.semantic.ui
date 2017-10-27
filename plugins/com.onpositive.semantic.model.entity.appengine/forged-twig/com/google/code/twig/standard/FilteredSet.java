package com.google.code.twig.standard;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.code.twig.Property;
import com.google.code.twig.translator.FilteredCollection;
import com.google.code.twig.translator.NotPredicate;
import com.onpositive.semantic.model.api.property.Predicate;

public class FilteredSet<T> extends FilteredCollection<T> implements Set<T>{

	public FilteredSet(Collection unfiltered, Predicate predicate) {
		super(unfiltered, predicate);
	}

		
}