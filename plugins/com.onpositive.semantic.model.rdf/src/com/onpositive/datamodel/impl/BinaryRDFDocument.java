package com.onpositive.datamodel.impl;

import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import com.onpositive.datamodel.core.BatchChange;
import com.onpositive.datamodel.core.DataStoreChange;
import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IDataStoreListener;
import com.onpositive.datamodel.core.IDocument;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.core.ProxyEntry;
import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.MultiValueTable;
import com.onpositive.datamodel.impl.storage.ObjectPool;
import com.onpositive.datamodel.impl.storage.PositiveIntToIntHashTable;
import com.onpositive.datamodel.impl.storage.SimpleStructuredStorage;
import com.onpositive.datamodel.model.ProxyProperty;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.IType;

public final class BinaryRDFDocument implements IDocument {

	private String url = ""; //$NON-NLS-1$

	private final HashMap<String, Integer> alternatives = new HashMap<String, Integer>();

	private final HashMap<Integer, String> backMap = new HashMap<Integer, String>();

	private final static Object[] noobjects = new Object[0];

	private final class RDFRealm implements IRealm<IEntry> {

		public void addRealmChangeListener(IRealmChangeListener<IEntry> listener) {
			BinaryRDFDocument.this.realmListeners.add(listener);
		}

		public boolean contains(Object o) {
			if (o instanceof IEntry) {
				return BinaryRDFDocument.this.contains((IEntry) o);
			}
			return false;
		}

		public Collection<IEntry> getContents() {
			final HashSet<IEntry> set = new HashSet<IEntry>();
			BinaryRDFDocument.this.entityTable
					.traverse(new PositiveIntToIntHashTable.Callback() {

						public boolean visit(int key, int value) {
							set.add(BinaryRDFDocument.this.createEntry(key));
							return true;
						}

					});
			return set;
		}

		public IRealm<IEntry> getParent() {
			return null;
		}

		public boolean isOrdered() {
			return false;
		}

		public void removeRealmChangeListener(
				IRealmChangeListener<IEntry> listener) {
			BinaryRDFDocument.this.realmListeners.remove(listener);
		}

		public int size() {
			return BinaryRDFDocument.this.entityTable.getSize();
		}

		public Iterator<IEntry> iterator() {
			return this.getContents().iterator();
		}
	}

	protected PositiveIntToIntHashTable entityTable = new PositiveIntToIntHashTable(
			100);

	protected WeakHashMap<Integer, Entity> entityCache = new WeakHashMap<Integer, Entity>();

	protected ObjectPool pool = new ObjectPool() {

		public int decodeValue(GrowingByteArray arrray, int offset,
				int position, Object[] result) {
			final int int1 = arrray.getInt(offset);
			result[position] = BinaryRDFDocument.this.createEntry(int1);
			return offset + 4;
		}

		public void encodeValue(GrowingByteArray array, Object value) {
			if (value instanceof IEntry) {
				final IEntry e = (IEntry) value;
				int id = BinaryRDFDocument.this.getId(e);
				if (id == 0) {
					id = BinaryRDFDocument.this.mapEntry(e);
				}
				array.add(id);
				return;
			}
			if (value instanceof IType) {
				final IType e = (IType) value;
				final String url2 = e.getId();
				int id = BinaryRDFDocument.this.getId(url2);
				if (id == 0) {
					id = BinaryRDFDocument.this.addEntry(url2);
				}
				array.add(id);
				return;
			}
			throw new RuntimeException();
		}

		public int skipValue(GrowingByteArray array, int offset) {
			return offset + 4;
		}

		
		public Class<?> resolveClass(ObjectStreamClass desc) {
			DataStoreRealm da = (DataStoreRealm) getRealm();
			return da.getDataModel().resolveClass(desc.getName());
		}

		
		public boolean isJavaProperty(String id) {
			DataStoreRealm da = (DataStoreRealm) getRealm();
			ProxyProperty property = (ProxyProperty) da.getProperty(id);
			if (property != null) {
				return property.isJava();
			}
			return false;
		}

	};

	SimpleStructuredStorage storage = new SimpleStructuredStorage(this.pool);

	private final HashSet<IRealmChangeListener<IEntry>> realmListeners = new HashSet<IRealmChangeListener<IEntry>>();

	private RDFRealm realm;

	private IDataStoreRealm dRealm;

	public IRealm<IEntry> getEntities() {
		if (realm != null) {
			return this.realm;
		}
		this.realm = new RDFRealm();
		return this.realm;
	}

	public BinaryRDFDocument() {

	}

	public String getUrl() {
		return this.url;
	}

	public String getUrl(Entity entity) {
		final String urle = this.backMap.get(entity.id);
		if (urle != null) {
			return urle;
		}
		return this.url + entity.id;
	}

	int getId(IEntry e) {
		if (e == null) {
			return 0;
		}
		if (e instanceof Entity) {
			final Entity ent = (Entity) e;
			if (ent.owner == this) {
				return ent.id;
			}
		}
		if (e instanceof ProxyEntry) {
			final ProxyEntry pm = (ProxyEntry) e;
			return this.getId(pm.getUnderlying());
		}
		final String url2 = e.getId();
		return this.getId(url2);
	}

	public int getId(String url) {
		final Integer long1 = this.alternatives.get(url);
		if (long1 != null) {
			return long1;
		}
		return 0;
	}

	private int mapEntry(IEntry e) {
		if (e instanceof ProxyEntry) {
			final ProxyEntry pm = (ProxyEntry) e;
			if (pm.getUnderlying() == null) {
				final Entity newEntry = (Entity) this.newEntry();
				pm.setUnderlying(newEntry);
				return newEntry.id;
			}
		}
		final String url2 = e.getId();
		final int newId = this.addEntry(url2);
		return newId;
	}

	private int addEntry(String url2) {
		final int newId = this.storage.newId();
		if (newId == Integer.MAX_VALUE) {
			throw new IllegalStateException("To lot entities was created here"); //$NON-NLS-1$
		}
		this.entityTable.put(newId, 1);

		this.alternatives.put(url2, newId);
		this.backMap.put(newId, url2);
		return newId;
	}

	public boolean contains(IEntry e) {
		final int id = this.getId(e);
		return (id != 0) && (this.entityTable.get(id) != 0);
	}

	public Object getValue(IEntry e, String property) {
		final int id = this.getId(e);
		if (id == 0) {
			return null;
		}
		return this.storage.getValue(id, property);
	}

	public Object[] getValues(IEntry e, String property) {
		final int id = this.getId(e);
		if (id == 0) {
			return noobjects;
		}
		return this.storage.getValues(id, property);
	}

	public void setValue(IEntry e, String property, Object value) {
		this.setValues(e, property, value);
	}

	public void setValues(IEntry e, String property, Object... values) {
		int id = this.getId(e);
		if (id == 0) {
			id = this.mapEntry(e);
		}
		if (this.dListeners != null) {
			for (final IDataStoreListener dl : this.dListeners) {
				dl.propertyChanged(this, e, property, values);
			}
		}
		this.storage.setValues(id, property, values);
	}

	public IEntry newEntry() {
		final int newId = this.storage.newId();
		if (newId == Integer.MAX_VALUE) {
			throw new IllegalStateException("To lot entities was created here"); //$NON-NLS-1$
		}
		final Entity createEntry = this.createEntry(newId);
		if (this.dListeners != null) {
			for (final IDataStoreListener dl : this.dListeners) {
				dl.entryAdding(this, createEntry);
			}
		}
		this.entityTable.put(newId, 1);
		return createEntry;
	}

	public int load(GrowingByteArray array, int position) {
		final StringBuilder bld = new StringBuilder();
		position = array.readString(position, bld);
		this.url = bld.toString();
		bld.delete(0, bld.length());
		position = this.entityTable.load(array, position);
		position = this.storage.load(array, position);
		this.alternatives.clear();
		this.backMap.clear();
		final int size = array.getInt(position);
		position += 4;
		for (int a = 0; a < size; a++) {
			position = array.readString(position, bld);
			final String uri = bld.toString();
			bld.delete(0, bld.length());
			final int id = array.getInt(position);
			this.alternatives.put(uri, id);
			this.backMap.put(id, uri);
			position += 4;
		}
		return position;
	}

	public synchronized void store(GrowingByteArray ba) {
		ba.add(this.url);
		ba.add(this.entityTable);
		ba.add(this.storage);
		ba.add(this.alternatives.size());
		for (final String s : this.alternatives.keySet()) {
			ba.add(s);
			ba.add(this.alternatives.get(s).intValue());
		}
	}

	public boolean hasValue(IEntry e, String property, Object value) {
		return Arrays.asList(this.getValues(e, property)).contains(value);
	}

	protected Entity createEntry(int key) {
		final Entity entity = this.entityCache.get(key);
		if (entity != null) {
			return entity;
		}
		final Entity entity2 = new Entity(key, BinaryRDFDocument.this);
		this.entityCache.put(key, entity2);
		return entity2;
	}

	public boolean contains(String id) {
		if (this.alternatives.containsKey(id)) {
			return true;
		}
		if (this.url == null) {
			return false;
		}
		if (!id.startsWith(this.url)) {
			return false;
		}
		final String substring = id.substring(this.url.length());
		final int parseInt = Integer.parseInt(substring);
		return (this.entityTable.get(parseInt) != 0);
	}

	public IEntry getEntry(String id) {
		final Integer integer = this.alternatives.get(id);
		if (integer != null) {
			return this.createEntry(integer);
		}
		if (!id.startsWith(this.url)) {
			return null;
		}
		final String substring = id.substring(this.url.length());
		final int parseInt = Integer.parseInt(substring);
		final int i = this.entityTable.get(parseInt);
		if (i != 0) {
			return this.createEntry(parseInt);
		}
		return null;
	}

	public IRealm<IEntry> getRealm() {
		if (this.dRealm != null) {
			return this.dRealm;
		}
		return this.realm;
	}

	public void setRealm(IDataStoreRealm ra) {
		this.dRealm = ra;
	}

	private HashSet<IDataStoreListener> dListeners;

	public void addDataStoreListener(IDataStoreListener listener) {
		if (this.dListeners == null) {
			this.dListeners = new HashSet<IDataStoreListener>();
		}
		this.dListeners.add(listener);
	}

	public void removeDataStoreListener(IDataStoreListener listener) {
		if (this.dListeners != null) {
			this.dListeners.remove(listener);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void batchChange(BatchChange parameterObject) {
		final HashSet<IDataStoreListener> dm = this.dListeners;
		this.dListeners = null;
		try {
			final HashDelta<IEntry> dlt = new HashDelta<IEntry>(Arrays
					.asList(parameterObject.toAdds), Collections.EMPTY_SET,
					Arrays.asList(parameterObject.toRemoves));
			for (final DataStoreChange e : parameterObject.changes) {
				HashDelta z = new HashDelta();
				z.markChanged(e.property);
				dlt.markChanged(e.entry, z);
			}
			for (final IEntry e : parameterObject.toAdds) {
				if (e instanceof ProxyEntry) {
					final ProxyEntry pm = (ProxyEntry) e;
					if (pm.getUnderlying() == null) {
						this.mapEntry(e);
					} else {
						if (!this.contains(pm.getUnderlying())) {
							final Entity ae = (Entity) pm.getUnderlying();
							if (ae.owner == this) {
								this.entityTable.put(ae.id, 1);
							}
						}
					}
				} else if (!this.contains(e)) {
					this.mapEntry(e);
				}
			}
			for (final IEntry e : parameterObject.toRemoves) {
				if (this.contains(e)) {
					this.removeEntry(e);
				}
			}
			for (final DataStoreChange c : parameterObject.changes) {
				this.setValues(c.entry, c.property, c.newValues);
			}
			if (dm != null) {
				for (final IDataStoreListener l : dm) {
					l.dataStoreChanged(this, dlt);
				}
			}
		} finally {
			this.dListeners = dm;
		}
	}

	public void removeEntry(IEntry e) {
		if (e instanceof Entity) {
			final Entity ent = (Entity) e;
			if (ent.owner == this) {
				this.entityTable.delete(ent.id);
				this.storage.remove(ent.id);
				if (this.dListeners != null) {
					for (final IDataStoreListener l : this.dListeners) {
						l.entryRemoved(this, e);
					}
				}
				return;
			}
		}
		final int id = this.getId(e);
		if (id != 0) {
			if (this.dListeners != null) {
				for (final IDataStoreListener l : this.dListeners) {
					l.entryRemoved(this, e);
				}
			}
			this.entityTable.delete(id);
			this.storage.remove(id);
		}
	}

	public boolean containsAbout(String property) {
		return this.storage.containsAbout(property);
	}

	public void getEntriesWith(String property, final Set<IEntry> toFill) {
		final MultiValueTable table = this.storage.vTable(property);
		if (table != null) {
			table.traverse(new PositiveIntToIntHashTable.Callback() {

				public boolean visit(int key, int value) {
					final Entity createEntry = BinaryRDFDocument.this
							.createEntry(key);
					toFill.add(createEntry);
					return true;
				}

			});
		}
	}

	public void getEntriesPointingTo(String propId, final IEntry e,
			final HashSet<IEntry> result) {
		final MultiValueTable table = this.storage.vTable(propId);
		if (table != null) {
			final int id = this.getId(e);
			if (id != 0) {
				table.traverse(new PositiveIntToIntHashTable.Callback() {

					public boolean visit(int key, int value) {
						int i = value - 1;
						if (i != -1) {
							final GrowingByteArray array = table.array;
							final int length = array.getInt(i);
							if (length < 0) {
								final Object[] objects = table.editedEntries
										.get(i);
								final int length2 = objects.length;
								for (int a = 0; a < length2; a++) {
									final Object object = objects[a];
									if (object instanceof IEntry) {
										final IEntry km = (IEntry) object;
										if (km.equals(e)) {
											result.add(BinaryRDFDocument.this
													.createEntry(key));
											return true;
										}
									}
								}
								return true;
							}
							i += 8;
							for (int a = 0; a < length; a++) {
								if (array.get(i) == MultiValueTable.CUSTOM) {
									final int candidate = array.getInt(i + 1);
									if (candidate == id) {
										result.add(BinaryRDFDocument.this
												.createEntry(key));
										return true;
									}
									i = i + 5;
								} else {
									i = table.skipValue(i);
								}
							}
						}
						return true;
					}

				});
				return;
			}
		}
	}

	public void getEntriesWithValue(String propId, final Object valueS,
			final Set<IEntry> result) {
		final MultiValueTable table = this.storage.vTable(propId);
		if (table != null) {
			table.traverse(new PositiveIntToIntHashTable.Callback() {

				public boolean visit(int key, int value) {
					Object[] values = table.getValues(key);
					if (values != null) {
						for (Object o : values) {
							if (o.equals(valueS)) {
								result.add(BinaryRDFDocument.this
										.createEntry(key));
							}
						}
					}
					return true;
				}

			});
			return;

		}
	}

	public Set<String> getKnownProperties() {
		return this.storage.getProps();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVersionNumber() {
		return this.storage.getVersion();
	}

}