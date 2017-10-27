package com.onpositive.datamodel.impl.storage;

import java.util.IdentityHashMap;
import java.util.Set;

public final class SimpleStructuredStorage implements IStorableInByteBuffer {

	private static final Object[] NO_OBJECT = new Object[] {};
	private int universeMaxId = 1;
	private final IdentityHashMap<String, MultiValueTable> properties = new IdentityHashMap<String, MultiValueTable>();
	private final ObjectPool pool;
	private int version;

	public SimpleStructuredStorage(ObjectPool pool) {
		this.pool = pool;
	}

	public int newId() {
		return this.universeMaxId++;
	}
	
	
	public Object[] getValues(int handle, String id) {
		final MultiValueTable multiValueTable = this.properties.get(id);
		if (multiValueTable == null) {
			return NO_OBJECT;
		}
		return multiValueTable.getValues(handle);
	}

	public void setValues(int handle, String id, Object... values) {
		MultiValueTable multiValueTable = this.properties.get(id);
		this.version++;
		if (multiValueTable == null) {
			multiValueTable = new MultiValueTable(this.pool);
			this.properties.put(id, multiValueTable);
		}
		multiValueTable.setValues(handle, values);
	}

	public int load(GrowingByteArray array, int position) {
		this.version = array.getInt(position);
		position += 4;
		this.universeMaxId = array.getInt(position);
		position += 4;
		final int size = array.getInt(position);
		position += 4;
		for (int a = 0; a < size; a++) {
			final StringBuilder bld = new StringBuilder();
			position = array.readString(position, bld);
			final String propId = bld.toString().intern();
			boolean javaProperty = pool.isJavaProperty(propId);
			final MultiValueTable multiValueTable = javaProperty?new JavaMutltiValueTable(this.pool):new MultiValueTable(
					this.pool);
			position = multiValueTable.load(array, position);
			this.properties.put(propId, multiValueTable);
		}
		return position;
	}

	public void store(GrowingByteArray ba) {
		ba.add(this.version);
		ba.add(this.universeMaxId);
		ba.add(this.properties.size());
		for (final String s : this.properties.keySet()) {
			final MultiValueTable vl = this.properties.get(s);
			ba.add(s);
			ba.add(vl);
		}
	}

	public Object getValue(int id, String string) {
		final MultiValueTable multiValueTable = this.properties.get(string);
		if (multiValueTable == null) {
			return null;
		}
		return multiValueTable.getValue(id);
	}

	public void remove(int id) {
		this.version++;
		for (final MultiValueTable v : this.properties.values()) {
			v.remove(id);
		}
	}

	public boolean containsAbout(String property) {
		return this.properties.containsKey(property);
	}

	public MultiValueTable vTable(String id) {
		return this.properties.get(id);
	}

	public Set<String> getProps() {
		return this.properties.keySet();
	}

	public int getVersion() {
		return this.version;
	}
}