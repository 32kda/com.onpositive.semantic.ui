package com.onpositive.semantic.model.api.realm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.query.Query;

public class SortingRealm extends ParentedRealm<Object> implements IDescribableToQuery{

	final Comparator relation;

	@SuppressWarnings("rawtypes")
	public SortingRealm(IRealm owner, Comparator<?>relation) {
		super(owner);
		this.relation=relation;
	}

	@Override
	public void dispose() {
		ObjectChangeManager.removeWeakListener(relation, this);
		super.dispose();		
	}

	@Override
	protected void startListening() {
		super.startListening();
		ObjectChangeManager.addWeakListener(relation, this);
		
		//PropertyAccess.addPropertyStructureListener(relation, this);
	}	

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<Object> getContentsInternal() {
		final ArrayList<Object> filteredContents = new ArrayList(owner.getContents());
		Collections.sort(filteredContents,relation);
		return filteredContents;
	}
	
	@Override
	public boolean mayHaveDublicates() {
		return true;
	}
	
	@Override
	public boolean contains(Object o) {
		return owner.contains(o);
	}



	@Override
	public boolean adapt(Query query) {
		if (relation instanceof IDescribableToQuery){
			IDescribableToQuery d=(IDescribableToQuery) relation;
			return d.adapt(query);
		}
		if (relation==null){
			return true;
		}
		return false;
	}

}
