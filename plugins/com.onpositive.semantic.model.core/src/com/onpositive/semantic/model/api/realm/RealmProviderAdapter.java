package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public abstract class RealmProviderAdapter<T> implements IRealmProvider<T> {

	@Override
	public IRealm<T> getRealm(IHasMeta model, Object parentObject, Object object) {
		return getRealm(model, parentObject);
	}
	
	public abstract IRealm<T>getRealm(IHasMeta meta, Object parent);

	public final IRealm<T> getRealm(){
		return getRealm(null, null,null);
	}
}
