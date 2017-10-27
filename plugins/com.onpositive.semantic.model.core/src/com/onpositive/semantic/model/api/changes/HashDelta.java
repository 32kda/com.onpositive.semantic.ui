/**
 * 
 */
package com.onpositive.semantic.model.api.changes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class HashDelta<T> implements ISetDelta<T>, Cloneable {

	private static final long serialVersionUID = 1638154391489354527L;
	
	private Collection<T> added = new LinkedHashSet<T>();
	private HashMap<T, ISetDelta<Object>> chHashSet = new HashMap<T, ISetDelta<Object>>();
	private Collection<T> rmHashSet = new LinkedHashSet<T>();

	@Override
	public Collection<T> getAddedElements() {
		return Collections.unmodifiableCollection(this.added);
	}
	protected boolean orderChanged;

	public boolean isOrderChanged() {
		return orderChanged;
	}

	public void setOrderChanged(boolean orderChanged) {
		this.orderChanged = orderChanged;
	}

	@Override
	public Collection<T> getChangedElements() {
		return this.chHashSet.keySet();
	}

	@Override
	public Collection<T> getRemovedElements() {
		return Collections.unmodifiableCollection(this.rmHashSet);
	}

	public static <T> ISetDelta<T>createDeltaWithout(ISetDelta<T>t,Collection<T> toIgnore){
		HashSet<T> addedElements = new HashSet<T>(t.getAddedElements());
		HashSet<T> changedElements = new HashSet<T>(t.getChangedElements());
		HashSet<T> removedElements = new HashSet<T>(t.getRemovedElements());
		addedElements.removeAll(toIgnore);
		changedElements.removeAll(toIgnore);
		removedElements.removeAll(toIgnore);
		HashDelta<T>newDelta=new HashDelta<T>(addedElements,changedElements,removedElements);		
		return newDelta;		
	}

	public ISetDelta<T> markRemoved(T eleement) {
		if (eleement == null) {
			return this;
		}
		this.rmHashSet.add(eleement);
		this.added.remove(eleement);
		this.chHashSet.remove(eleement);
		return this;
	}

//	public <C> HashDelta<C> transform(ITransformer<T, C> transformer) {
//		HashDelta<C> cm = new HashDelta<C>();
//		for (T d : added) {
//			C transform = transformer.transform(d);
//			if (transform!=null){
//			cm.markAdded(transform);
//			}
//		}
//		for (T d : rmHashSet) {
//			C transform = transformer.transform(d);
//			if (transform!=null){
//			cm.markRemoved(transform);
//			}
//		}
//		for (T d : chHashSet.keySet()) {
//			C transform = transformer.transform(d);
//			if (transform!=null){
//				cm.markChanged(transform);
//			}
//		}
//		return cm;
//	}

	@Override
	public HashDelta<T> clone() {
		HashDelta<T> hashDelta = new HashDelta<T>();
		hashDelta.added = new HashSet<T>(added);
		hashDelta.chHashSet = new HashMap<T, ISetDelta<Object>>(chHashSet);
		hashDelta.rmHashSet = new HashSet<T>(rmHashSet);
		return hashDelta;
	}

	public ISetDelta<T> markAdded(T eleement) {
		if (eleement == null) {
			return this;
		}
		this.added.add(eleement);
		this.chHashSet.remove(eleement);
		this.rmHashSet.remove(eleement);
		return this;
	}

	public ISetDelta<T> markChanged(T eleement) {
		if (eleement == null) {
			return this;
		}
		this.chHashSet.put(eleement, null);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ISetDelta<T> markChanged(T eleement, ISetDelta dlt) {
		if (this.chHashSet.containsKey(eleement)) {
			HashDelta<Object> setDelta = (HashDelta<Object>) this.chHashSet
					.get(eleement);
			if (setDelta == null) {
				setDelta = new HashDelta<Object>();
				chHashSet.put(eleement, setDelta);
			}
			if (dlt != null) {
				setDelta.added.addAll(dlt.getAddedElements());
				setDelta.rmHashSet.removeAll(dlt.getAddedElements());
				setDelta.rmHashSet.addAll(dlt.getRemovedElements());
				for (final Object o : dlt.getChangedElements()) {
					setDelta.markChanged(o, dlt.getSubDelta(o));
				}
			}
		} else {
			this.chHashSet.put(eleement, dlt);
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		return this.added.isEmpty() && this.rmHashSet.isEmpty()
				&& this.chHashSet.isEmpty();
	}

	public HashDelta() {
		super();
	}

	public HashDelta(Collection<T> addedElements,
			Collection<T> changedElements, Collection<T> removedElements) {
		this.added = addedElements;
		this.chHashSet = new HashMap<T, ISetDelta<Object>>(changedElements
				.size());
		for (final T e : changedElements) {
			this.markChanged(e);
		}
		this.rmHashSet = removedElements;
	}

	public HashDelta(T element) {
		this.chHashSet.put(element,null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashDelta buildFrom(Collection old, Collection newOne) {
		final HashDelta a = new HashDelta();
		a.added.addAll(newOne);
		if (newOne.size()>100&&!(newOne instanceof Set)){
			newOne=new HashSet(newOne);
		}
		for (final Object e : old) {
			if (!newOne.contains(e)) {
				a.markRemoved(e);
			}
			a.added.remove(e);
		}
		return a;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> HashDelta<T> buildChecked(Collection<T> old,
			Collection<T> newOne) {
		final HashDelta a = new HashDelta();
		a.added.addAll(newOne);
		for (final Object e : old) {
			if (!newOne.contains(e)) {
				a.markRemoved(e);
			}
			a.added.remove(e);
		}
		return a;
	}

	@Override
	public ISetDelta<Object> getSubDelta(T element) {
		return this.chHashSet.get(element);
	}

	public static <T> HashDelta<T> createAdd(T entry) {
		final HashDelta<T> rs = new HashDelta<T>();
		rs.markAdded(entry);
		return rs;
	}

	public static <T> HashDelta<T> createRemove(T entry) {
		final HashDelta<T> rs = new HashDelta<T>();
		rs.markRemoved(entry);
		return rs;
	}

	public static <T> HashDelta<T> createChanged(T entry) {
		final HashDelta<T> rs = new HashDelta<T>();
		rs.markChanged(entry);
		return rs;
	}

	public static <T> HashDelta<T> buildChanged(T[] elements) {
		final HashDelta<T> rs = new HashDelta<T>();
		for (T r : elements) {
			rs.markChanged(r);
		}
		return rs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> ISetDelta<T> buildAdd(
			Collection<T> allDocuments) {
		final HashDelta<T> rs = new HashDelta<T>(allDocuments,(Collection)Collections.emptySet(),(Collection)Collections.emptySet());
		return rs;
	}

	protected Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static<T> ISetDelta<T> buildRemove(Collection<T> allDocuments) {
		final HashDelta<T> rs = new HashDelta<T>((Collection)Collections.emptySet(),(Collection)Collections.emptySet(),allDocuments);
		return rs;		
	}

	@Override
	/**
	 * Returns first added element or <code>null</code> if no added elements present 
	 */
	public Object getFirstAddedElement() {
		if (added.size() > 0) {
			return added.iterator().next();
		}
		return null;
	}

	@Override
	/**
	 * Returns first removed element or <code>null</code> if no removed elements present 
	 */
	public Object getFirstRemovedElement() {
		if (rmHashSet.size() > 0) {
			return rmHashSet.iterator().next();
		}
		return null;
	}

	@Override
	/**
	 * Returns first changed element or <code>null</code> if no changed elements present 
	 */
	public Object getFirstChangedElement() {
		if (chHashSet.size() > 0) {
			return chHashSet.keySet().iterator().next();
		}
		return null;
	}
}