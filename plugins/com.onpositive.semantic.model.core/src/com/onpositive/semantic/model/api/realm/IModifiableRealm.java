package com.onpositive.semantic.model.api.realm;


public interface IModifiableRealm<T> extends IRealm<T> {

	void add(T element);

	void remove(T element);

	void add(Iterable<T> element);

	void remove(Iterable<T> element);

	boolean isReadOnly();
}