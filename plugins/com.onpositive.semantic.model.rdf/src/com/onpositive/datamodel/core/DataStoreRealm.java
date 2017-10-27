package com.onpositive.datamodel.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.onpositive.commons.Activator;
import com.onpositive.datamodel.impl.AbstractOneValuePropertyStore;
import com.onpositive.datamodel.impl.Entity;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.datamodel.model.CalculatablePropertyHost;
import com.onpositive.datamodel.model.DataModel;
import com.onpositive.datamodel.model.DefaultModelProperty;
import com.onpositive.datamodel.model.ExecutableCommand;
import com.onpositive.datamodel.model.ICalculatableProperty;
import com.onpositive.datamodel.model.IPropertyCalculator;
import com.onpositive.datamodel.model.ProxyProperty;
import com.onpositive.datamodel.model.ProxyProvider;
import com.onpositive.datamodel.model.ValueClass;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandWithUndoContext;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyLookup;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.api.undo.IChangeManager;
import com.onpositive.semantic.model.api.undo.IExecutableOperationUndoable;
import com.onpositive.semantic.model.api.undo.UndoRedoSupportExtension;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ISupportsExternalChangeNotify;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.ITypedRealm;

public class DataStoreRealm implements IDataStoreRealm,
		IPropertyProvider, IPropertyCalculator {

	public static class DataStoreModification {
		HashSet<IEntry> additions = new HashSet<IEntry>();
		HashSet<IEntry> deletions = new HashSet<IEntry>();
		ArrayList<DataStoreChange> changes = new ArrayList<DataStoreChange>();
	
		public BatchChange getChange() {
			final IEntry[] adds = new IEntry[this.additions.size()];
			this.additions.toArray(adds);
			final IEntry[] dels = new IEntry[this.deletions.size()];
			this.deletions.toArray(dels);
			final DataStoreChange[] chs = new DataStoreChange[this.changes
					.size()];
			this.changes.toArray(chs);
			final BatchChange batchChange = new BatchChange(adds, dels, chs);
			return batchChange;
		}
	
		public DataStoreModification revertModification() {
			final DataStoreModification modification = new DataStoreModification();
			modification.additions.addAll(this.deletions);
			modification.deletions.addAll(this.additions);
			for (final DataStoreChange c : this.changes) {
				modification.changes.add(c.revert());
			}
			return modification;
		}
	}

	public class RealmChange implements IExecutableOperationUndoable {
	
		private final HashMap<IDataStore, DataStoreModification> modifications = new HashMap<IDataStore, DataStoreModification>();
		private final Object undodable;
	
		public RealmChange(Object undoContext) {
			this.undodable = undoContext;
		}
	
		public DataStoreModification getModification(IDataStore store) {
			DataStoreModification dataStoreModification = this.modifications
					.get(store);
			if (dataStoreModification == null) {
				dataStoreModification = new DataStoreModification();
				this.modifications.put(store, dataStoreModification);
			}
			return dataStoreModification;
		}
	
		public void delete(IDataStore s, IEntry target) {
			this.getModification(s).deletions.add(target);
		}
	
		public void add(IDataStore s, IEntry target) {
			this.getModification(s).additions.add(target);
		}
	
		public void setValue(IDataStore s, IEntry target, String prop,
				Object[] values, Object[] oldValues) {
			this.getModification(s).changes.add(new DataStoreChange(target,
					values, oldValues, prop));
		}
	
		public RealmChange revert() {
			final RealmChange ra = new RealmChange(this.undodable);
			for (final IDataStore a : this.modifications.keySet()) {
				ra.modifications.put(a, this.modifications.get(a)
						.revertModification());
			}
			return ra;
		}
	
		public IStatus execute() {
			DataStoreRealm.this.isExecuting = true;
			DataStoreRealm.this.transactionDelta = new HashDelta<IEntry>();
			try {
				Set<IDataStore> keySet = this.modifications.keySet();
				HashSet<IDataStore> ex = new HashSet<IDataStore>();
				for (IDataStore s : keySet) {
					if (s instanceof AbstractOneValuePropertyStore) {
						ex.add(s);
					} else {
						final DataStoreModification dataStoreModification = this.modifications
								.get(s);
						final BatchChange change = dataStoreModification
								.getChange();
						s.batchChange(change);
					}
				}
				for (final IDataStore s : ex) {
					final DataStoreModification dataStoreModification = this.modifications
							.get(s);
					final BatchChange change = dataStoreModification
							.getChange();
					s.batchChange(change);
				}
				return Status.OK_STATUS;
			} catch (final Exception e) {
				return new Status(IStatus.ERROR, DataModelPlugin.getInstance()
						.getBundle().getSymbolicName(), IStatus.ERROR, e
						.getMessage(), e);
			} finally {
				DataStoreRealm.this
						.fireDelta(DataStoreRealm.this.transactionDelta);
				DataStoreRealm.this.transactionDelta = null;
				DataStoreRealm.this.isExecuting = false;
			}
		}
	
		public IStatus undo() {
			return this.revert().execute();
		}
	
		public IStatus redo() {
			return this.execute();
		}
	
		public boolean isEmpty() {
			return this.modifications.isEmpty();
		}
	
		public Object getUndoContext() {
			return this.undodable;
		}
	
		public Object getTarget() {
			return null;
		}
	
	}

	public class ObjectChange {
		private final IEntry e;
	
		public ObjectChange(IEntry e) {
			this.e = e;
			propChanges = new HashMap<String, HashDelta<Object>>();
			values = new HashMap<String, Set<Object>>();
		}
	
		public ObjectChange(ObjectChange ob, boolean isNew, boolean isDeleted) {
			this.e = ob.e;
			this.isNew = isNew;
			this.isDeleted = isDeleted;
			this.propChanges = ob.propChanges;
			this.values = ob.values;
		}
	
		public IDataStoreRealm getRealm() {
			return DataStoreRealm.this;
		}
	
		boolean isNew;
		boolean isDeleted;
	
		private HashMap<String, HashDelta<Object>> propChanges;
		private HashMap<String, Set<Object>> values;
	
		public void recordCreation() {
			this.isNew = true;
		}
	
		public void recordDeletion() {
			this.isNew = false;
			this.isDeleted = true;
		}
	
		HashDelta<Object> getOrCreate(String id) {
			HashDelta<Object> hashDelta = this.propChanges.get(id);
			if (hashDelta == null) {
				hashDelta = new HashDelta<Object>();
				this.propChanges.put(id, hashDelta);
			}
			return hashDelta;
		}
	
		public Set<Object> getCurrentValue(String prop) {
			Set<Object> set = this.values.get(prop);
			if (set == null) {
				set = DataStoreRealm.this.getValues(this.e, prop);
				this.values.put(prop, set);
			}
			return set;
		}
	
		public Set<Object> getValue(String prop) {
			Set<Object> currentValue = this.getCurrentValue(prop);
			final HashDelta<Object> hashDelta = this.propChanges.get(prop);
			if (hashDelta != null) {
				currentValue = new HashSet<Object>(currentValue);
				currentValue.addAll(hashDelta.getAddedElements());
				currentValue.removeAll(hashDelta.getRemovedElements());
			}
			return currentValue;
		}
	
		@SuppressWarnings("unchecked")
		public void recordPropertySetValues(String propId, Object[] values) {
			final Set<Object> currentValue = this.getCurrentValue(propId);
			for (int a = 0; a < values.length; a++) {
				IEntry entry = ProxyProvider.getEntry(values[a]);
				if (entry != null) {
					values[a] = entry;
				}
			}
			final ArrayList<Object> vl = new ArrayList<Object>(Arrays
					.asList(values));
			vl.remove(null);
			final HashDelta<Object> buildFrom = HashDelta.buildFrom(
					currentValue, vl);
			this.propChanges.put(propId, buildFrom);
		}
	
		@SuppressWarnings("unchecked")
		public void recordPropertySetValue(String propId, Object value) {
			if (value instanceof Set) {
				recordPropertySetValues(propId, ((Set) value).toArray());
				return;
			}
			IEntry entry = ProxyProvider.getEntry(value);
			if (entry != null) {
				value = entry;
			}
			final Set<Object> currentValue = this.getCurrentValue(propId);
	
			if (value == null) {
				final HashDelta buildFrom = HashDelta.buildFrom(currentValue,
						Collections.emptySet());
				this.propChanges.put(propId, buildFrom);
				return;
			}
			final HashDelta buildFrom = HashDelta.buildFrom(currentValue,
					Collections.singleton(value));
			if (!buildFrom.isEmpty()) {
				this.propChanges.put(propId, buildFrom);
			}
		}
	
		public void recordPropertyAddValue(String propId, Object value) {
			if (value == null) {
				return;
			}
			final Set<Object> currentValue = this.getValue(propId);
			if (!currentValue.contains(value)) {
				this.getOrCreate(propId).markAdded(value);
			}
		}
	
		public void recordPropertyRemoveValue(String propId, Object value) {
			if (value == null) {
				return;
			}
			final Set<Object> currentValue = this.getValue(propId);
			if (currentValue.contains(value)) {
				this.getOrCreate(propId).markRemoved(value);
			}
		}
	
		public boolean isEmpty() {
			if (this.isDeleted || this.isNew || !this.propChanges.isEmpty()) {
				return false;
			}
			return true;
		}
	
		public boolean isDelete() {
			return isDeleted;
		}
	
		public boolean isNew() {
			return isNew;
		}
	
	}

	class TypeRealm implements ITypedRealm<IEntry> {
	
		private final IType ts;
		HashSet<IEntry> contents;
		HashSet<IRealmChangeListener<IEntry>> lst = new HashSet<IRealmChangeListener<IEntry>>();
		public int refCount;
	
		public TypeRealm(IType type) {
			this.ts = type;
			this.getContents();
		}
	
		public void addRealmChangeListener(IRealmChangeListener<IEntry> listener) {
			this.lst.add(listener);
		}
	
		public boolean contains(Object o) {
			if (o instanceof IEntry) {
				final IEntry e = (IEntry) o;
				return e.isInstance(this.ts);
			}
			return false;
		}
	
		public Collection<IEntry> getContents() {
			if (this.contents == null) {
				this.contents = new HashSet<IEntry>();
				for (final IEntry e : DataStoreRealm.this) {
					if (e.isInstance(this.ts)) {
						this.contents.add(e);
					}
				}
			}
			return this.contents;
		}
	
		public IRealm<IEntry> getParent() {
			return null;
		}
	
		public boolean isOrdered() {
			return false;
		}
	
		public void removeRealmChangeListener(
				IRealmChangeListener<IEntry> listener) {
			this.lst.remove(listener);
		}
	
		public int size() {
			return this.getContents().size();
		}
	
		public Iterator<IEntry> iterator() {
			return this.getContents().iterator();
		}
	
		public void processDelta(HashDelta<?> buildFrom) {
			if (buildFrom == null) {
				return;
			}
			final Collection<IEntry> contents2 = this.getContents();
			HashDelta<IEntry> dlt = null;
			for (final Object o : buildFrom.getAddedElements()) {
				final IEntry r = (IEntry) o;
				if (r.isInstance(this.ts)) {
					if (dlt == null) {
						dlt = new HashDelta<IEntry>();
					}
					dlt.markAdded(r);
					contents2.add(r);
				}
			}
			for (final Object o : buildFrom.getRemovedElements()) {
				final IEntry r = (IEntry) o;
				if (contents.contains(r) || r.isInstance(this.ts)) {
					if (dlt == null) {
						dlt = new HashDelta<IEntry>();
					}
					dlt.markRemoved(r);
					contents2.remove(r);
				}
			}
			for (final Object z : buildFrom.getChangedElements()) {
				final IEntry r = (IEntry) z;
				if (!r.isInstance(this.ts)) {
					final boolean remove = contents2.remove(r);
					if (remove) {
						if (dlt == null) {
							dlt = new HashDelta<IEntry>();
						}
						dlt.markRemoved(r);
					}
				} else {
					if (dlt == null) {
						dlt = new HashDelta<IEntry>();
					}
					if (!contents.contains(r)) {
						dlt.markAdded(r);
						contents.add(r);
					} else {
						dlt.markChanged(r, ((HashDelta) buildFrom)
								.getSubDelta(r));
					}
				}
			}
			if (dlt != null) {
				this.fire(dlt);
			}
		}
	
		private void fire(HashDelta<IEntry> dlt) {
			for (final IRealmChangeListener<IEntry> e : this.lst) {
				e.realmChanged(this, dlt);
			}
		}
	
		public String getId(IEntry object) {
			return object.getId();
		}
	
		public IEntry getObject(String id) {
			return DataStoreRealm.this.getObject(id);
		}
	
		public ICommand getObjectAdditionCommand(IEntry object) {
			final CompositeCommand cm = new CompositeCommand();
			cm.addCommand(DataStoreRealm.this.getObjectAdditionCommand(object));
			cm.addCommand(DataStoreRealm.this.getProperty(DataModel.TYPE).getCommandFactory().createAddValueCommand(null, object, this.ts));
			return cm;
		}
	
		public ICommand getObjectDeletionCommand(IEntry id) {
			return DataStoreRealm.this.getObjectDeletionCommand(id);
		}
	
		public Collection<IProperty> getProperties() {
			return DataStoreRealm.this.getProperties();
		}
	
		public IProperty getProperty(String name) {
			return DataStoreRealm.this.getProperty(name);
		}
	
		public IEntry newObject() {
			return DataStoreRealm.this.newObject();
		}
	
		public void execute(ICommand cmd) {
			DataStoreRealm.this.execute(cmd);
		}
	
		public IPropertyLookup getPropertyProvider() {
			return DataStoreRealm.this;
		}
	
		public IType getType() {
			return this.ts;
		}
	
		public IType getType(String string) {
			return DataStoreRealm.this.getType(string);
		}
	
		public ICommand createSetTypeCommand(Object object, IType type) {
			return DataStoreRealm.this.createSetTypeCommand(object, type);
		}
	
	}

	private final ArrayList<IDataStore> stores = new ArrayList<IDataStore>();

	private WeakReference<HashSet<IEntry>> contentsReference;

	private final DataModel dataModel;

	private final ProxyProperty typeProperty;

	private Object owner;

	IDataStore primary;

	IChangeManager manager;

	private final HashMap<IType, TypeRealm> typeMap = new HashMap<IType, TypeRealm>();
	
	public final IChangeManager getChangeManager() {
		return this.manager;
	}

	public final void setChangeManager(IChangeManager manager) {
		this.manager = manager;
	}

	public DataStoreRealm(DataModel model) {
		this.dataModel = model;
		this.typeProperty = (ProxyProperty) this.getProperty(DataModel.TYPE);
	}

	public DataModel getDataModel() {
		return this.dataModel;
	}

	private final HashSet<IRealmChangeListener<IEntry>> er = new HashSet<IRealmChangeListener<IEntry>>();

	private HashMap<String, IProperty> entries;

	@SuppressWarnings("unchecked")
	public void removeDataStore(IDataStore store) {
		if (store instanceof AbstractOneValuePropertyStore) {
			AbstractOneValuePropertyStore ps = (AbstractOneValuePropertyStore) store;
			exclusives.remove(ps.getProperty());
		}
		store.removeDataStoreListener(this.storeListener);
		final Set<IEntry> contents = (Set<IEntry>) this.getContents();
		
		
		this.stores.remove(store);
		
		if (this.stores.size() == 1) {
			this.primary = this.stores.iterator().next();
		} else {
			this.primary = null;
		}
		if (this.contentsReference != null) {
			this.contentsReference=null;
		}
		final Collection<IEntry> contents2 = getContents();
		final HashDelta buildFrom = HashDelta.buildFrom(contents, contents2);
		this.fireDelta(buildFrom);
	}

	private void inferChanged(HashSet<IEntry> additional,
			Set<? extends Object> toProcess) {
		for (Object o : toProcess) {
			if (o instanceof IEntry) {
				IEntry r = (IEntry) o;
				Set<IType> types = r.getTypes();
				for (IType t : types) {
					ValueClass valueClass = this.dataModel.getValueClass(t
							.getId().intern());
					if (valueClass.isBroadCastChanges()) {
						HashSet<IEntry> hashSet = new HashSet<IEntry>();
						HashSet<IEntry> toInfer = new HashSet<IEntry>();
						findAllReferers(r, hashSet);
						for (IEntry e : hashSet) {
							if (!additional.contains(e)) {
								additional.add(e);
								toInfer.add(e);
							}

						}
						if (toInfer != null) {
							inferChanged(additional, toInfer);
						}
					}

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void fireDelta(HashDelta buildFrom) {
		HashSet<IEntry> additionalEntries = new HashSet<IEntry>();
		inferChanged(additionalEntries, (Set<Object>) buildFrom
				.getChangedElements());
		for (IEntry e : additionalEntries) {
			buildFrom.markChanged(e);
		}
		fireDeltaWithoutInference(buildFrom);
	}

	public void fireDeltaWithoutInference(HashDelta buildFrom) {
		for (final TypeRealm t : this.typeMap.values()) {
			t.processDelta(buildFrom);
		}
		for (final IRealmChangeListener<IEntry> e : new HashSet<IRealmChangeListener<IEntry>>(
				this.er)) {
			e.realmChanged(this, buildFrom);
		}
		this.fireDeltaThrowsListeners(buildFrom);
	}

	IDataStoreListener storeListener = new IDataStoreListener() {

		public synchronized void entryAdding(IDataStore ds, IEntry entry) {

			if (!DataStoreRealm.this.contains(entry)) {
				if (DataStoreRealm.this.contentsReference != null) {
					final HashSet<IEntry> hashSet = DataStoreRealm.this.contentsReference
							.get();
					if (hashSet != null) {
						hashSet.add(entry);
					}
				}
				if (DataStoreRealm.this.isExecuting) {
					DataStoreRealm.this.transactionDelta.markAdded(entry);
				} else {
					DataStoreRealm.this.fireDelta(HashDelta.createAdd(entry));
				}
			}

		}

		public synchronized void entryRemoved(IDataStore ds, IEntry entry) {
			if (!DataStoreRealm.this.isExecuting) {
				if (!DataStoreRealm.this.contains(entry)) {
					if (DataStoreRealm.this.contentsReference != null) {
						final HashSet<IEntry> hashSet = DataStoreRealm.this.contentsReference
								.get();
						if (hashSet != null) {
							hashSet.remove(entry);
						}
					}
					if (DataStoreRealm.this.isExecuting) {
						DataStoreRealm.this.transactionDelta.markRemoved(entry);
					} else {
						DataStoreRealm.this.fireDelta(HashDelta
								.createRemove(entry));
					}
				}
			} else {

			}
		}

		@SuppressWarnings("unchecked")
		public synchronized void propertyChanged(IDataStore ds, IEntry e,
				String property, Object[] values) {
			if (!DataStoreRealm.this.isExecuting) {
				DataStoreRealm.this.fireDelta(HashDelta.createChanged(e));
			} else {
				HashDelta dlt = new HashDelta();
				dlt.markChanged(property);
				DataStoreRealm.this.transactionDelta.markChanged(e, dlt);
			}
		}

		public synchronized void dataStoreChanged(IDataStore ds,
				ISetDelta<IEntry> entry) {
			HashSet<IEntry> cache = null;
			if (DataStoreRealm.this.contentsReference != null) {
				cache = DataStoreRealm.this.contentsReference.get();
			}
			final HashDelta<IEntry> dlt = DataStoreRealm.this.isExecuting ? DataStoreRealm.this.transactionDelta
					: new HashDelta<IEntry>();
			ArrayList<IDataStore> toCheck = null;
			if (DataStoreRealm.this.primary == null) {
				toCheck = new ArrayList<IDataStore>();
				for (final IDataStore s : DataStoreRealm.this.stores) {
					if (s != ds) {
						toCheck.add(s);
					}
				}
			}
			for (final IEntry e : entry.getAddedElements()) {
				if (DataStoreRealm.this.primary != null) {
					dlt.markAdded(e);
					if (cache != null) {
						cache.add(e);
					}
				} else {
					boolean allReady = false;
					for (final IDataStore sm : toCheck) {
						if (sm.contains(e)) {
							allReady = true;
						}
					}
					if (!allReady) {
						dlt.markAdded(e);
						if (cache != null) {
							cache.add(e);
						}
					}
				}
			}
			for (final IEntry e : entry.getRemovedElements()) {
				if (!DataStoreRealm.this.contains(e)) {
					if (cache != null) {
						cache.remove(e);
					}
					dlt.markRemoved(e);
				}
			}
			for (final IEntry r : entry.getChangedElements()) {
				dlt.markChanged(r, (ISetDelta) entry.getSubDelta(r));
			}
			if (!DataStoreRealm.this.isExecuting) {
				DataStoreRealm.this.fireDelta(dlt);
			}
		}

	};

	@SuppressWarnings("unchecked")
	public void addDataStore(IDataStore store) {
		store.setRealm(this);
		store.addDataStoreListener(this.storeListener);
		if (store instanceof AbstractOneValuePropertyStore) {
			AbstractOneValuePropertyStore ps = (AbstractOneValuePropertyStore) store;
			exclusives.put(ps.getProperty(), ps);
		}
		final Set<IEntry> contents = (Set<IEntry>) (this.er.isEmpty() ? Collections
				.emptySet()
				: this.getContents());
		this.stores.add(store);
		if (this.stores.size() == 1) {
			this.primary = store;
		} else {
			this.primary = null;
		}
		if (this.contentsReference != null) {
			this.contentsReference=null;
		}
		
		final Collection<IEntry> contents2 = this.getContents();
		final HashDelta buildFrom = HashDelta
				.buildFrom(contents, contents2);
		this.fireDelta(buildFrom);
		
	}

	public void addRealmChangeListener(IRealmChangeListener<IEntry> listener) {
		this.er.add(listener);
	}

	public boolean contains(Object o) {
		if (o instanceof IEntry) {
			final IEntry obj2 = (IEntry) o;
			final IEntry er = obj2;
			if (this.contentsReference != null) {
				final HashSet<IEntry> hashSet = this.contentsReference.get();
				if (hashSet != null) {
					return hashSet.contains(o);
				}
			}
			for (final IDataStore s : this.stores) {
				if (s.contains(er)) {
					return true;
				}
			}
		}
		return false;
	}

	public Collection<IEntry> getContents() {
		if (this.contentsReference != null) {
			final HashSet<IEntry> hashSet = this.contentsReference.get();
			if (hashSet != null) {
				return hashSet;
			}
		}
		final HashSet<IEntry> entry = new HashSet<IEntry>();
		for (final IDataStore s : this.stores) {
			entry.addAll(s.getEntities().getContents());
		}
		this.contentsReference = new WeakReference<HashSet<IEntry>>(entry);
		return entry;
	}

	public IRealm<IEntry> getParent() {
		return null;
	}

	public boolean isOrdered() {
		return false;
	}

	public void removeRealmChangeListener(IRealmChangeListener<IEntry> listener) {
		this.er.remove(listener);
	}

	public int size() {
		return this.getContents().size();
	}

	public Iterator<IEntry> iterator() {
		return this.getContents().iterator();
	}

	public String getId(IEntry object) {
		return object.getId();
	}

	public IEntry getObject(String id) {
		for (final IDataStore s : this.stores) {
			if (s.contains(id)) {
				return s.getEntry(id);
			}
		}
		return null;
	}

	public Object getValue(Object obj, String propId) {
		ProxyProperty property = (ProxyProperty) getProperty(propId);
		if (property != null) {
			if (property.isJava()) {
				return getValues(obj, propId);
			}
		}
		if (obj instanceof IEntry) {
			final IEntry obj2 = (IEntry) obj;
			IEntry e = obj2;
			if (e instanceof ProxyEntry) {
				final ProxyEntry pa = (ProxyEntry) e;
				if (pa.underlying == null) {
					return null;
				}
				e = pa.underlying;
			}
			for (final IDataStore s : this.stores) {
				final Object value = s.getValue(e, propId);
				if (value != null) {
					return value;
				}
			}
		}

		if (property.isCalculatable()) {
			return property.getValue(obj);
		}
		return null;
	}

	public int getValueCount(Object obj, String propId) {
		return this.internalGetValues(obj, propId).size();
	}

	public Set<Object> getValues(Object obj, String propId) {
		Set<Object> internalGetValues = internalGetValues(obj, propId);
		return internalGetValues;
	}

	private Set<Object> internalGetValues(Object obj, String propId) {
		if (obj instanceof IEntry) {
			IEntry e = (IEntry) obj;
			if (e instanceof ProxyEntry) {
				final ProxyEntry pa = (ProxyEntry) e;
				if (pa.underlying == null) {
					return Collections.emptySet();
				}
				e = pa.underlying;
			}
			Object value = null;
			HashSet<Object> values = null;
			if (this.primary != null) {
				final Object[] valuesArray = this.primary.getValues(e, propId);
				if (valuesArray.length == 1) {
					return Collections.singleton(valuesArray[0]);
				}
				if (valuesArray.length == 0) {
					return Collections.emptySet();
				}
				values = new HashSet<Object>();
				for (final Object o : valuesArray) {
					values.add(o);
				}
				return values;
			} else {
				for (final IDataStore s : this.stores) {
					final Object[] valuesArray = s.getValues(e, propId);
					if ((valuesArray != null) && (valuesArray.length > 0)) {
						if (valuesArray.length == 1) {
							if (values == null) {
								if (value == null) {
									value = valuesArray[0];
								} else {
									values = new HashSet<Object>();
									values.add(value);
									values.add(valuesArray[0]);
								}
							} else {
								values.add(valuesArray[0]);
							}
						} else {
							if (values == null) {
								values = new HashSet<Object>();
							}
							for (final Object o : valuesArray) {
								values.add(o);
							}
						}
					}
				}
			}
			if (values != null) {
				return values;
			}
			if (value != null) {
				return Collections.singleton(value);
			}
		}
		return Collections.emptySet();
	}

	public boolean hasValue(Object obj, Object value, String propId) {
		IEntry e = (IEntry) obj;
		if (e instanceof ProxyEntry) {
			final ProxyEntry pa = (ProxyEntry) e;
			if (pa.underlying == null) {
				return false;
			}
			e = pa.underlying;
		}
		for (final IDataStore s : this.stores) {
			if (s.hasValue(e, propId, value)) {
				return true;
			}
		}
		return false;
	}

	public ICommand getObjectAdditionCommand(IEntry object) {
		return new SimpleOneArgCommand(this, object, SimpleOneArgCommand.ADD,
				null);
	}

	public ICommand getObjectDeletionCommand(IEntry id) {
		return new SimpleOneArgCommand(this, id, SimpleOneArgCommand.DELETE,
				null);
	}

	public Collection<IProperty> getProperties() {
		if (this.entries == null) {
			this.entries = new HashMap<String, IProperty>();
			for (final DefaultModelProperty p : this.dataModel.getProperties()) {
				ICalculatableProperty calculator = p.getCalculator();
				if (calculator != null) {
					this.entries.put(p.getId().intern(),
							new CalculatablePropertyHost(calculator, this, p,
									this));
				} else {
					this.entries.put(p.getId().intern(), new ProxyProperty(
							this, p, this));
				}
			}
		}
		return this.entries.values();
	}

	public IProperty getProperty(String name) {
		this.getProperties();
		return (IProperty) this.entries.get(name.intern());
	}

	public IEntry newObject() {
		return new ProxyEntry(this);
	}

	private boolean isExecuting;
	private HashDelta<IEntry> transactionDelta;

	public synchronized void execute(ICommand cmd) {
		Object undoContext = null;
		if (cmd instanceof ICommandWithUndoContext) {
			final ICommandWithUndoContext c = (ICommandWithUndoContext) cmd;
			undoContext = c.getUndoContext();
		}
		boolean undo = true;
		if (cmd instanceof ExecutableCommand) {
			ExecutableCommand c = (ExecutableCommand) cmd;
			undo = c.isUndoable();
			if (undo && undoContext == null) {
				undoContext = UndoRedoSupportExtension.getDefaultContext();
			}
		}
		final HashMap<IEntry, ObjectChange> changes = new HashMap<IEntry, ObjectChange>();
		this.internalExecute(cmd, changes);

		inferChanges(changes);

		final RealmChange ch = this.createRealmChange(changes, undoContext);
		if (ch.isEmpty()) {
			return;
		}
		if (this.manager != null && undo) {
			this.manager.execute(ch);
		} else {
			ch.execute();
		}
	}

	@SuppressWarnings("unchecked")
	private void inferChanges(final HashMap<IEntry, ObjectChange> changes) {
		HashMap<ValueClass, HashMap<IEntry, ObjectChange>> inferred = new HashMap<ValueClass, HashMap<IEntry, ObjectChange>>();
		for (IEntry e : changes.keySet()) {
			ObjectChange objectChange = changes.get(e);
			Set<Object> currentValue = objectChange
					.getCurrentValue(DataModel.TYPE);
			Set<Object> value = objectChange.getValue(DataModel.TYPE);
			HashSet<Object> allTypes = new HashSet<Object>();
			allTypes.addAll(value);
			allTypes.addAll(currentValue);
			Set<IType> types = (Set) allTypes;
			for (IType t : types) {
				ValueClass valueClass = dataModel.getValueClass(t.getId());
				boolean contains = currentValue.contains(t);
				boolean contains2 = value.contains(t);
				boolean isNew = contains2 && !contains;
				boolean isDeleted = contains && !contains2;
				if (valueClass != null) {
					Set<ValueClass> superClasses = valueClass.getSuperClasses();
					for (ValueClass va : superClasses) {
						processClassDelta(inferred, e, objectChange, va, isNew,
								isDeleted);
					}
					processClassDelta(inferred, e, objectChange, valueClass,
							isNew, isDeleted);
				}
			}
		}
		for (ValueClass v : inferred.keySet()) {
			HashMap<IEntry, ObjectChange> hashMap = inferred.get(v);
			for (IInstanceListener l : v.getInstanceListeners()) {
				Map<IEntry, ObjectChange> processDelta = l.processDelta(this,
						hashMap);
				if (processDelta != null && !processDelta.isEmpty()) {
					changes.putAll(processDelta);
				}
			}
		}
	}

	private void processClassDelta(
			HashMap<ValueClass, HashMap<IEntry, ObjectChange>> inferred,
			IEntry e, ObjectChange objectChange, ValueClass valueClass,
			boolean contains, boolean contains2) {
		Iterable<IInstanceListener> instanceListeners = valueClass
				.getInstanceListeners();
		if (instanceListeners != null) {
			HashMap<IEntry, ObjectChange> hashMap = inferred.get(valueClass);
			if (hashMap == null) {
				hashMap = new HashMap<IEntry, ObjectChange>();
				inferred.put(valueClass, hashMap);
			}
			hashMap.put(e, new ObjectChange(objectChange, contains, contains2));
		}
	}

	private HashMap<String, IDataStore> exclusives = new HashMap<String, IDataStore>();

	private RealmChange createRealmChange(
			HashMap<IEntry, ObjectChange> changes, Object undoContext) {
		final RealmChange ch = new RealmChange(undoContext);
		l2: for (final ObjectChange c : changes.values()) {
			for (final IDataStore s : this.stores) {
				if (c.isNew) {
					ch.add(s, c.e);
				} else if (c.isDeleted) {
					ch.delete(s, c.e);
				}
			}
			for (final String key : c.propChanges.keySet()) {
				final HashDelta<Object> hashDelta = c.propChanges.get(key);
				if (!hashDelta.isEmpty()) {
					final HashSet<Object> currentValue = new HashSet<Object>(c
							.getCurrentValue(key));
					final Object[] oldValues = currentValue.toArray();
					currentValue.removeAll(hashDelta.getRemovedElements());
					currentValue.addAll(hashDelta.getAddedElements());
					final Object[] array = currentValue.toArray();

					for (final IDataStore s : this.stores) {
						if (wantsToStore(s, key)) {
							ch.setValue(s, c.e, key, array, oldValues);
						}
					}
				}
			}
			if (c.isDeleted) {
				continue l2;
			}
		}
		return ch;
	}

	private boolean wantsToStore(IDataStore s, String key) {
		IDataStore dataStore = exclusives.get(key);
		if (dataStore != null) {
			return dataStore == s;
		}
		return true;
	}

	static Object[] NO_VALUE = new Object[0];

	public void findAllReferers(IEntry v, HashSet<IEntry> result) {
		for (final IDataStore s : this.stores) {
			final Set<String> knownProperties = s.getKnownProperties();

			for (final String sm : knownProperties) {
				s.getEntriesPointingTo(sm, v, result);
			}
		}
	}

	private void internalExecute(ICommand cmd, HashMap<IEntry, ObjectChange> ch) {
		if (cmd instanceof CompositeCommand) {
			final CompositeCommand c = (CompositeCommand) cmd;
			for (final ICommand cm : c) {
				this.internalExecute(cm, ch);
			}
		} else if (cmd instanceof SimpleOneArgCommand) {
			final SimpleOneArgCommand argCommand = (SimpleOneArgCommand) cmd;
			final String kind = argCommand.getKind();
			if (kind == SimpleOneArgCommand.ADD) {
				final IEntry value = (IEntry) argCommand.getValue();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				objectChange.recordCreation();
			} else if (kind == SimpleOneArgCommand.DELETE) {
				final IEntry value = (IEntry) argCommand.getValue();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				// query data stores for values to remove;
				for (final IDataStore s : this.stores) {
					final Set<String> knownProperties = s.getKnownProperties();

					for (final String sm : knownProperties) {
						final HashSet<IEntry> rm = new HashSet<IEntry>();
						final Object[] la = s.getValues(value, sm);
						if ((la != null) && (la.length > 0)) {
							for (final Object o : la) {
								objectChange.recordPropertyRemoveValue(sm, o);
							}
						}
						s.getEntriesPointingTo(sm, value, rm);
						for (final IEntry e : rm) {
							ObjectChange removePropertyChange = ch.get(e);
							if (removePropertyChange == null) {
								removePropertyChange = new ObjectChange(e);
								ch.put(e, removePropertyChange);
							}
							removePropertyChange.recordPropertyRemoveValue(sm,
									value);
						}
					}
				}

				objectChange.recordDeletion();
			} else if (kind == SimpleOneArgCommand.ADD_VALUE) {
				final IEntry value = (IEntry) argCommand.getTarget();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				objectChange.recordPropertyAddValue(
						((DefaultModelProperty) argCommand.getExecutor())
								.getId(), argCommand.getValue());
			} else if (kind == SimpleOneArgCommand.REMOVE_VALUE) {
				final IEntry value = (IEntry) argCommand.getTarget();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				objectChange.recordPropertyRemoveValue(
						((DefaultModelProperty) argCommand.getExecutor())
								.getId(), argCommand.getValue());
			} else if (kind == SimpleOneArgCommand.REMOVE_ALL_VALUES) {
				final IEntry value = (IEntry) argCommand.getTarget();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				objectChange.recordPropertySetValue(
						((DefaultModelProperty) argCommand.getExecutor())
								.getId(), NO_VALUE);
			} else if (kind == SimpleOneArgCommand.SET_VALUE) {
				final IEntry value = (IEntry) argCommand.getTarget();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);

					objectChange.recordPropertySetValue(
							((DefaultModelProperty) argCommand.getExecutor())
									.getId(), argCommand.getValue());
					if (!objectChange.isEmpty()) {
						ch.put(value, objectChange);
					}
				} else {
					objectChange.recordPropertySetValue(
							((DefaultModelProperty) argCommand.getExecutor())
									.getId(), argCommand.getValue());
				}
			} else if (kind == SimpleOneArgCommand.SET_VALUES) {
				final IEntry value = (IEntry) argCommand.getTarget();
				ObjectChange objectChange = ch.get(value);
				if (objectChange == null) {
					objectChange = new ObjectChange(value);
					ch.put(value, objectChange);
				}
				objectChange.recordPropertySetValues(
						((DefaultModelProperty) argCommand.getExecutor())
								.getId(), (Object[]) argCommand.getValue());
			}
		}
	}

	public Collection<IDataStore> getStores() {
		return new ArrayList<IDataStore>(this.stores);
	}

	static WeakHashMap<IRealm<?>, Collection<IValueListener<Object>>> realms = new WeakHashMap<IRealm<?>, Collection<IValueListener<Object>>>();
	private final HashMap<Object, Collection<IValueListener<Object>>> objects = new HashMap<Object, Collection<IValueListener<Object>>>();

	public void addValueListener(Object o, IValueListener<Object> vl) {
		synchronized (objects) {
			Collection<IValueListener<Object>> collection = this.objects.get(o);
			if (collection == null) {
				collection = new HashSet<IValueListener<Object>>(1);
				this.objects.put(o, collection);
			}
			collection.add(vl);
		}
	}

	public void removeValueListener(Object o, IValueListener<Object> vl) {
		synchronized (this.objects) {
			final Collection<IValueListener<Object>> collection = this.objects
					.get(o);
			if (collection == null) {
				return;
			}
			collection.remove(vl);

			if (collection.isEmpty()) {
				this.objects.remove(o);
			}
		}

	}

	public void registerRealm(IRealm<?> rs, IValueListener<Object> listener) {
		synchronized (realms) {
			Collection<IValueListener<Object>> collection = realms.get(rs);
			if (collection == null) {
				collection = new HashSet<IValueListener<Object>>(1);
				realms.put(rs, collection);
			}
			collection.add(listener);
		}
	}

	public void unregisterRealm(IRealm<?> rs, IValueListener<Object> listener) {
		synchronized (realms) {
			final Collection<IValueListener<Object>> collection = realms
					.get(rs);
			if (collection == null) {
				return;
			}
			collection.remove(listener);
			if (collection.isEmpty()) {
				realms.remove(rs);
			}
		}
	}

	private void fireDeltaThrowsListeners(HashDelta<Object> dlt) {
		Collection<Object> changedElements = dlt.getChangedElements();
		HashSet<IRealm<?>> hashSet = null;
		synchronized (realms) {
			hashSet = new HashSet<IRealm<?>>(realms.keySet());
		}
		for (final IRealm<?> e : hashSet) {
			if (e instanceof ISupportsExternalChangeNotify) {
				final ISupportsExternalChangeNotify ss = (ISupportsExternalChangeNotify) e;
				final HashDelta<Object> dlt1 = new HashDelta<Object>();
				for (final Object o : changedElements) {
					if (e.contains(o)) {
						dlt1.markChanged(o);
					}
				}
				if (!dlt1.isEmpty()) {
					ss.changed(dlt1);
				}
			}
		}
		JavaObjectManager.fireExternalDelta(dlt);
		for (final Object o : changedElements) {
			ArrayList<IValueListener<Object>> arrayList = null;
			synchronized (this.objects) {
				final Collection<IValueListener<Object>> collection = this.objects
						.get(o);
				if (collection != null) {
					arrayList = new ArrayList<IValueListener<Object>>(
							collection);
				}
			}
			if (arrayList != null) {
				for (final IValueListener<Object> v : arrayList) {
					try {
						v.valueChanged(null, o);
					} catch (Exception e) {
						Activator.log(e);
					}
				}
			}
		}
	}

	public Set<IProperty> getProperties(ValueClass obj) {
		final HashSet<IProperty> prs = new HashSet<IProperty>();
		for (final String s : obj.getPropertyIds()) {
			prs.add(this.getProperty(s));
		}
		return prs;
	}

	@SuppressWarnings("unchecked")
	public Iterable<IProperty> getProperties(Object obj) {
		final IEntry e = (IEntry) obj;
		final Set<IType> types = this.getTypes(e);
		final HashSet<ProxyProperty> result = new HashSet<ProxyProperty>();
		for (final IType ea : types) {
			final String url = ea.getId();
			final ValueClass valueClass = this.dataModel.getValueClass(url);
			if (valueClass != null) {
				final Set<String> propertyIds = valueClass.getPropertyIds();
				for (final String s : propertyIds) {
					final IProperty property = this
							.getProperty(s);
					result.add((ProxyProperty) property);
				}
			}
		}
		return (Iterable) result;
	}

	@SuppressWarnings("unchecked")
	public Set<IType> getTypes(IEntry entity) {
		return (Set) this.typeProperty.getValues(entity);
	}

	public Set<IEntry> getEntriesWith(IProperty property) {
		final HashSet<IEntry> result = new HashSet<IEntry>();
		for (final IDataStore s : this.stores) {
			s.getEntriesWith(property.getId(), result);
		}
		return result;
	}

	public Set<IEntry> getEntriesPointingTo(String propId, IEntry value) {
		final HashSet<IEntry> result = new HashSet<IEntry>();
		for (final IDataStore s : this.stores) {
			s.getEntriesPointingTo(propId, value, result);
		}
		return result;
	}

	public boolean isInstance(Entity entity, IType type) {
		final Set<IType> types = this.getTypes(entity);
		if (types.contains(type)) {
			return true;
		}
		final String typeUrl = type.getId().intern();
		for (final IType e : types) {
			final String url = e.getId().intern();
			final ValueClass valueClass = this.dataModel.getValueClass(url);
			if (valueClass != null) {
				if (valueClass.getId().equals(typeUrl)) {
					return true;
				}
				final boolean superClassOf = valueClass.isSuperClassOf(typeUrl);
				if (superClassOf) {
					return true;
				}
			}
		}
		return false;
	}

	public IType getType(String string) {
		final ValueClass valueClass = this.dataModel.getValueClass(string);
		if (valueClass == null) {
			return this.dataModel.getValueClass(string.intern());
		}
		return valueClass;
	}

	public ITypedRealm<IEntry> getTypeRealm(IType type) {
		TypeRealm typeRealm = this.typeMap.get(type);
		if (typeRealm == null) {
			typeRealm = new TypeRealm(type);

			this.typeMap.put(type, typeRealm);
		}
		typeRealm.refCount++;
		return typeRealm;
	}

	public void disconnectTypeRealm(IRealm<IEntry> tr) {
		final TypeRealm tm = (TypeRealm) tr;
		tm.refCount--;
		if (tm.refCount == 0) {
			this.typeMap.remove(tm.ts);
		}
	}

	public ITypedRealm<IEntry> getTypeRealm(String type) {
		final IType type2 = this.getType(type);
		if (type2 == null) {
			return null;
		}
		return this.getTypeRealm(type2);
	}

	public ICommandExecutor getCommandExecutor() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public IProperty getProperty(
			Object obj, String name) {
		return (IProperty) this.getProperty(name);
	}

	public IPropertyLookup getPropertyProvider() {
		return this;
	}

	public void setOwner(Object configurableDocumentSystem) {
		this.owner = configurableDocumentSystem;
	}

	public Object getOwner() {
		return this.owner;
	}

	public Set<IEntry> findEntries(String propertyId, Object value) {
		HashSet<IEntry> result = new HashSet<IEntry>();
		for (IDataStore s : stores) {
			s.getEntriesWithValue(propertyId, value, result);
		}
		return result;
	}

	public ObjectChange newObjectChange(IEntry r) {
		return new ObjectChange(r);
	}

	public void recordChange(Object owner2, String id, Object value) {
		HashMap<IEntry, ObjectChange> changes = new HashMap<IEntry, ObjectChange>();
		ObjectChange c = new ObjectChange((IEntry) owner2);
		Set<Object> currentValue = c.getCurrentValue(id);
		c.recordPropertySetValues(id, currentValue.toArray());
		final RealmChange ch = this.createRealmChange(changes, null);

		ch.execute();
	}

	public ICommand createSetTypeCommand(Object obj, IType type) {
		IProperty property = this.getProperty(DataModel.TYPE);
		return property.getCommandFactory().createAddValueCommand(property, obj, type);
	}
}