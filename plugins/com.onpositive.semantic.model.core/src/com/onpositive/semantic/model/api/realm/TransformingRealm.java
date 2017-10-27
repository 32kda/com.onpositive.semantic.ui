package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;

public class TransformingRealm extends ParentedRealm<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final IProperty relation;

	@SuppressWarnings("rawtypes")
	public TransformingRealm(IRealm owner, IProperty relation) {
		super(owner);
		this.relation=relation;
	}
	
	public IProperty getProperty()
	{
		return relation;
	}

	@Override
	public void dispose() {
		PropertyAccess.removePropertyStructureListener(relation, this);
		super.dispose();		
	}

	@Override
	protected void startListening() {
		super.startListening();
		PropertyAccess.addPropertyStructureListener(relation, this);
	}	

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<Object> getContentsInternal() {
		final LinkedHashSet<Object> filteredContents = new LinkedHashSet<Object>();
		Collection<Object> contents = this.owner.getContents();
		for (final Object e : contents) {			
			filteredContents.add(relation.getValue(e));			
		}
		return filteredContents;
	}
	
	@Override
	public boolean mayHaveDublicates() {
		return false;
	}

}
