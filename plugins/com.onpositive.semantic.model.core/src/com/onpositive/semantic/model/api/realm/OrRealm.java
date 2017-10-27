package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.query.Query;


public class OrRealm<T> extends AbstractRealm<T> implements IDescribableToQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashSet<IRealm<T>> realms = new HashSet<IRealm<T>>();
	private IRealmChangeListener<T> listener=new IRealmChangeListener<T>() {

		
		@Override
		public void realmChanged(IRealm<T> realmn, ISetDelta<T> delta) {
					
		}
	};
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processChange(){
		if (isListening()){
			Collection<T> contents = cached;
			Collection<T> internalGet = internalGet();
			HashDelta buildFrom = HashDelta.buildFrom(contents, internalGet);
			fireDelta(buildFrom);
			cached=internalGet;
		}	
	}
	
	public OrRealm() {

	}

	public void addRealm(IRealm<T> a) {
		this.realms.add(a);
		processChange();
	}

	public void removeRealm(IRealm<T> b) {
		this.realms.remove(b);
		processChange();
	}
	
	Collection<T>cached;

	public Collection<T> internalGet() {
		final HashSet<T> el = new HashSet<T>();
		for (final IRealm<T> e : this.realms) {
			el.addAll(e.getContents());
		}
		return el;
	}
	

	@Override
	public IRealm<T> getParent() {
		return null;
	}

	@Override
	public int size() {
		return this.getContents().size();
	}

	@Override
	public boolean isOrdered() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		if (cached!=null){
			return cached.contains(o);
		}
		return internalContains(o);
	}

	protected boolean internalContains(Object o) {
		for (final IRealm<T> r : this.realms) {
			if (r.contains(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return this.getContents().iterator();
	}

	public Collection<IRealm<T>>getRealms(){
		return new HashSet<IRealm<T>>(realms);
	}

	@Override
	public boolean mayHaveDublicates() {		
		return false;
	}
	
	
	@Override
	protected void startListening() {
		for (IRealm<T>l:realms){
			l.addRealmChangeListener(listener);
		}
		cached=internalGet();
	}
	
	@Override
	protected void stopListening() {
		for (IRealm<T>l:realms){
			l.removeRealmChangeListener(listener);
		}
		cached=null;
	}

	
	@Override
	public Collection<T> getContents() {
		if (cached!=null){
			return cached;
		}
		return internalGet();
	}

	@Override
	public boolean adapt(Query query) {
		
		return false;
	}
}
