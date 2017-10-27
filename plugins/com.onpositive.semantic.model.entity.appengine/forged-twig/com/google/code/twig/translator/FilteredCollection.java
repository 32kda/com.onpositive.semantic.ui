package com.google.code.twig.translator;

import java.util.Collection;
import java.util.Iterator;

import com.onpositive.semantic.model.Iterators;
import com.onpositive.semantic.model.api.property.Predicate;

public class FilteredCollection<E> implements Collection<E> {
	final Collection<E> unfiltered;
	final Predicate<? super E> predicate;

	public FilteredCollection(Collection<E> unfiltered, Predicate<? super E> predicate) {
		this.unfiltered = unfiltered;
		this.predicate = predicate;
	}

	public FilteredCollection<E> createCombined(Predicate<? super E> newPredicate) {
		return new FilteredCollection<E>(unfiltered, new AndPredicate(
				predicate, newPredicate));
		// .<E> above needed to compile in JDK 5
	}

	@Override
	public boolean add(E element) {

		return unfiltered.add(element);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {

		return unfiltered.addAll(collection);
	}

	@Override
	public void clear() {
		unfiltered.clear();
	}

	@Override
	public boolean contains(Object element) {
		try {
			// unsafe cast can result in a CCE from predicate.apply(), which we
			// will catch
			@SuppressWarnings("unchecked")
			E e = (E) element;

			/*
			 * We check whether e satisfies the predicate, when we really mean
			 * to check whether the element contained in the set does. This is
			 * ok as long as the predicate is consistent with equals, as
			 * required.
			 */
			return predicate.apply(e) && unfiltered.contains(element);
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		for (Object element : collection) {
			if (!contains(element)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return !Iterators.any(unfiltered.iterator(), predicate);
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.filter(unfiltered.iterator(), predicate);
	}

	@Override
	public boolean remove(Object element) {
		try {
			// unsafe cast can result in a CCE from predicate.apply(), which we
			// will catch
			@SuppressWarnings("unchecked")
			E e = (E) element;

			// See comment in contains() concerning predicate.apply(e)
			return predicate.apply(e) && unfiltered.remove(element);
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
	}

	
	@Override
	public int size() {
		return Iterators.size(iterator());
	}

	@Override
	public Object[] toArray() {
		// creating an ArrayList so filtering happens once
		return Iterators.newArrayList(iterator()).toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return Iterators.newArrayList(iterator()).toArray(array);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
}