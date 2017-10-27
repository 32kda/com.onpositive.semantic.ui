package com.onpositive.semantic.ui.businessdroids;

import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;
import com.onpositive.semantic.model.api.realm.IFilter;

public class SemanticUIFilterWrapper implements com.onpositive.businessdroids.model.filters.IFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1152286532833198395L;
	final IFilter semanticFilter ;

	public SemanticUIFilterWrapper(IFilter semanticFilter) {
		super();
		this.semanticFilter = semanticFilter;
	}

	@Override
	public void save(IStore store) {}
	
	@Override
	public void load(IStore store) throws NoSuchElement {}
	
	@Override
	public boolean matches(Object record) {
		return semanticFilter.accept( record );
	}
	
	@Override
	public String getTitle() {
		return semanticFilter.toString() ;
	}

	public IFilter getSemanticFilter() {
		return semanticFilter;
	}

}
