package com.onpositive.commons.platform.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;

/**
 * 
 * @author kor
 * 
 */
public class RegistryMap<T extends GenericRegistryObject> implements
		Iterable<T> {

	private final String point;
	private List<T> sorted;
	private final Class<T> ownerClass;
	protected HashMap<String, T> map;

	public RegistryMap(String point, Class<T> ownerClass) {
		this.point = point;
		this.ownerClass = ownerClass;
	}

	public T get(String id) {
		this.checkLoad();
		return this.map.get(id); 
	}
	
	protected Map<String,T>all(){
		checkLoad();
		return map;
	}

	public Iterator<T> iterator() {
		this.checkLoad();
		return this.sorted.iterator();
	}

	protected void checkLoad() {
		if (this.map == null) {
			this.map = new HashMap<String, T>();
			this.initMap(this.map);
			this.sorted = new ArrayList<T>(this.map.values());
			Collections.sort(this.sorted);
			this.sorted = Collections.unmodifiableList(this.sorted);
		}
		
	}

	/**
	 * fills map of elements;
	 * 
	 * @param map
	 */
	protected void initMap(HashMap<String, T> map) {
		try {
			final Constructor<T> cm = this.ownerClass.getConstructor(IConfigurationElement.class);
			for (final IConfigurationElement el : Platform.getExtensionRegistry().getConfigurationElementsFor(this.point)) {
				
				try {
					final T newInstance = cm.newInstance(el);
					final String id = newInstance.getId();
					if (map.containsKey(id)) {
						System.err.println(id + "is dublicated " + this);
					}
					map.put( id, newInstance );
				} catch (final IllegalArgumentException e) {
					map = null;
					throw new IllegalStateException();
				} catch (final InstantiationException e) {
					map = null;
					throw new IllegalStateException();
				} catch (final IllegalAccessException e) {
					map = null;
					throw new IllegalStateException();
				} catch (final InvocationTargetException e) {
					map = null;
					throw new IllegalStateException();
				}
			}
		} catch (final SecurityException e) {
			map = null;
			throw new IllegalStateException();
		} catch (final NoSuchMethodException e) {
			map = null;
			throw new IllegalStateException();
		}
	}

}
