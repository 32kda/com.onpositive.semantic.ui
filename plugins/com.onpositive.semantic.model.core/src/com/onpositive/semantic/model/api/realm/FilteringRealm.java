package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.query.Query;

@SuppressWarnings({ "unchecked" })
public class FilteringRealm<T> extends  ParentedRealm<T> implements IDescribableToQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final IFilter relation;

	public FilteringRealm(IRealm<T> owner, IFilter relation) {
		super(owner);
		this.relation = relation;
	}

	@Override
	public void dispose() {
		relation.removeValueListener(this);
		super.dispose();		
	}

	@Override
	protected void startListening() {
		super.startListening();
		relation.addValueListener(this);
	}	

	
	@Override
	protected Collection<T> getContentsInternal() {
		final LinkedHashSet<T> filteredContents = new LinkedHashSet<T>();
		Collection<T> contents = this.owner.getContents();
		for (final T e : contents) {			
			if (this.relation.accept(e)) {
				filteredContents.add(e);
			}
		}
		return filteredContents;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized void realmChanged(IRealm realmn, ISetDelta delta) {
		final HashDelta newDelta = new HashDelta();
		
		if (delta==null){
			super.realmChanged(realmn, delta);
			return;
		}
		if (delta.isOrderChanged()){
			filteredContents=null;
			newDelta.setOrderChanged(true);
		}
		for (final Object e : delta.getAddedElements()) {
			if (this.relation.accept(e)) {
				newDelta.markAdded(e);
				if (filteredContents != null) {
					filteredContents.add(e);
				}
			}
		}
		for (final Object e : delta.getChangedElements()) {
			if (this.relation.accept(e)) {
				if (filteredContents != null) {
					if (filteredContents.add(e)) {
						newDelta.markAdded(e);
					} else {
						newDelta.markChanged(e);
					}
				} else {
					newDelta.markChanged(e);
				}
			}
			else{
				if (filteredContents != null) {
					if (filteredContents.remove(e)){
						newDelta.markRemoved(e);			
					}					
				}
			}
		}
		for (final Object e : delta.getRemovedElements()) {
			if (this.relation.accept(e)) {
				if (filteredContents != null) {
					if (filteredContents.remove(e)) {
						newDelta.markRemoved(e);
					}
				} else {
					newDelta.markRemoved(e);
				}
			}
		}
		fireDelta(newDelta);
	}

	@Override
	public boolean contains(Object o) {
		if (filteredContents != null) {
			return filteredContents.contains(o);
		}
		if (this.owner.contains(o)) {
			return this.relation.accept(o);
		}
		return false;
	}

	

	@Override
	public boolean mayHaveDublicates() {
		return false;
	}

	
	@Override
	public boolean adapt(Query query) {
		if (relation instanceof IDescribableToQuery){
			IDescribableToQuery rs=(IDescribableToQuery) relation;
			return rs.adapt(query);
		}
		return false;
	}
}
