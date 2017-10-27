package com.onpositive.datamodel.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ITypedRealm;

public class ProxyRealm<T> implements IRealm<T> {

	IRealmChangeListener<IEntry> realmChangeListener = new IRealmChangeListener<IEntry>() {

		public void realmChanged(IRealm<IEntry> realmn, ISetDelta<IEntry> delta) {
			notifyChanges(delta);
		}
	};

	private ITypedRealm<IEntry> base;
	private Class<T> clazz;
	private WeakHashMap<IEntry, T> proxies = new WeakHashMap<IEntry, T>();
	private HashSet<IRealmChangeListener<T>> listeners = new HashSet<IRealmChangeListener<T>>();

	private DataStoreRealm store;

	public ProxyRealm(Class<T> clazz, IDataStoreRealm base) {
		this.store = (DataStoreRealm) base;
		this.base = base.getTypeRealm(clazz.getAnnotation(TypeName.class)
				.value());
		this.clazz = clazz;
		base.addRealmChangeListener(realmChangeListener);
	}
	
	public void startTransaction(boolean undoable){
		ProxyProvider.startTransaction(new ExecutableCommand(store,undoable));
	}
	
	public void addObject(T object){
		ExecutableCommand current = ProxyProvider.getCurrent();
		ICommand objectAdditionCommand = base.getObjectAdditionCommand(ProxyProvider.getEntry(object));
		if (current==null){
			store.execute(objectAdditionCommand);
		}
		else{			
			current.addCommand(objectAdditionCommand);
		}
	}
	
	public void commitTransaction(){
		ProxyProvider.commitTransaction();		
	}
	
	public void cancelTransaction(){
		ProxyProvider.cancelTransaction();
	}
	
	

	public T newObject() {
		T proxy = getProxy(store.newObject());		
		return proxy;
	}
	
	public void delete(T object){
		ICommand objectDeletionCommand = store.getObjectDeletionCommand(ProxyProvider.getEntry(object));
		ExecutableCommand current = ProxyProvider.getCurrent();		
		if (current==null){
			store.execute(objectDeletionCommand);
		}
		else{			
			current.addCommand(objectDeletionCommand);
		}
	}

	private T getProxy(IEntry e) {
		T t = proxies.get(e);
		if (t != null) {
			return t;
		}
		T createProxy = ProxyProvider.createProxy(clazz, e);
		proxies.put(e, createProxy);
		return createProxy;
	}

	protected void notifyChanges(ISetDelta<IEntry> delta) {
		if (listeners.size() > 0) {
			HashDelta<T> dlt = new HashDelta<T>();
			for (IEntry e : delta.getAddedElements()) {
				dlt.markAdded(getProxy(e));
			}
			for (IEntry e : delta.getRemovedElements()) {

				dlt.markRemoved(getProxy(e));
			}
			for (IEntry e : delta.getChangedElements()) {
				if (!delta.getAddedElements().contains(e)) {
					ISetDelta<Object> subDelta = delta.getSubDelta(e);
					dlt.markChanged(getProxy(e), (ISetDelta<T>) subDelta);
				}
			}
			for (IRealmChangeListener<T> l : listeners) {
				l.realmChanged(this, dlt);
			}
		}
	}

	public void addRealmChangeListener(IRealmChangeListener<T> listener) {
		listeners.add(listener);
	}

	public void dispose() {
		base.removeRealmChangeListener(realmChangeListener);
	}

	public void removeRealmChangeListener(IRealmChangeListener<T> listener) {
		listeners.remove(listener);
	}

	public boolean contains(Object o) {
		if (clazz.isInstance(o)) {
			return base.contains(ProxyProvider.getEntry(o));
		}
		return false;
	}

	public Collection<T> getContents() {
		HashSet<T> es = new HashSet<T>();
		for (IEntry e : base.getContents()) {
			es.add(getProxy(e));
		}
		return es;
	}

	public IRealm<T> getParent() {
		return null;
	}

	public boolean isOrdered() {
		return base.isOrdered();
	}

	public int size() {
		return base.size();
	}

	public Iterator<T> iterator() {
		final Iterator<IEntry> bs = base.iterator();
		return new Iterator<T>() {

			public boolean hasNext() {
				return bs.hasNext();
			}

			public T next() {
				return getProxy(bs.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
}
