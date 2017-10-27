package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.query.Query;

public class ObjectListeningRealm extends ParentedRealm<Object> implements IDescribableToQuery,IDisposable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObjectListeningRealm() {
		super(null);
	}
	@SuppressWarnings("rawtypes")
	public ObjectListeningRealm(IRealm r) {
		super(null);
		setParent(r);
	}
	
	public void setParent(IRealm<?>r){
		Collection<Object> contents = getContents();
		Collection<?> contents2 = r.getContents();
		this.owner=r;
		this.parentMeta=owner.getMeta();
		this.cachedService=null;
		this.revisionId++;
		fireDelta(HashDelta.buildFrom(contents, contents2));
	}

	
	@Override
	public boolean mayHaveDublicates() {
		return getParent()!=null?getParent().mayHaveDublicates():false;
	}
	
	@Override
	public boolean isOrdered() {
		return getParent()!=null?getParent().isOrdered():false;
	}
	@Override
	public synchronized Collection<Object> getContents(){
		IRealm<Object> parent = getParent();
		if (parent!=null){
			return parent.getContents();
		}
		return Collections.emptySet();
	}
	@Override
	public int size() {
		IRealm<Object> parent = getParent();
		if (parent!=null){
			return parent.size();
		}
		return 0;
	}
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	public synchronized void realmChanged(IRealm realmn, ISetDelta delta) {
		fireDelta(delta);
	}
	
	
	@Override
	public boolean contains(Object o) {
		IRealm<Object> parent = getParent();
		if (parent!=null){
			return parent.contains(o);
		}
		return false;
	}
	
	@Override
	protected void startListening() {
		super.startListening();
		ObjectChangeManager.registerRealm(this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ObjectChangeManager.unregisterRealm(this);
	}

	
	@Override
	protected Collection<Object> getContentsInternal() {
		if (getParent()!=null){
			return getParent().getContents();
		}
		return Collections.emptySet();
	}
	
	@Override
	public boolean adapt(Query query) {
		return true;
	}

}
