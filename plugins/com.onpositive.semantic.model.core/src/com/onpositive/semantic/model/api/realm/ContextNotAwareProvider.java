package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public abstract class ContextNotAwareProvider<T> implements IRealmProvider<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	abstract IRealm<T> getRealm(IHasMeta model, Object parentObject);
	
	@Override
	public IRealm<T> getRealm(IHasMeta model, Object parentObject, Object object) {
		return getRealm(model, parentObject);
	}
}
