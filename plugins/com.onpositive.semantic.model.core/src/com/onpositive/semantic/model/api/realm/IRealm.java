package com.onpositive.semantic.model.api.realm;

import java.util.Collection;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface IRealm<T> extends Iterable<T>,IHasMeta {

	Collection<T> getContents();

	void addRealmChangeListener(IRealmChangeListener<T> listener);

	void removeRealmChangeListener(IRealmChangeListener<T> listener);

	IRealm<T> getParent();

	int size();

	boolean isOrdered();
	
	boolean mayHaveDublicates();

	boolean contains(Object o);
	
}