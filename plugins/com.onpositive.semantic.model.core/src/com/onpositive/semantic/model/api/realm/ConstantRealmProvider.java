package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public class ConstantRealmProvider extends RealmProviderAdapter<Object>{

	private Object[] args;
	private OrderedRealm<Object> realm;

	public ConstantRealmProvider(Object...args){
		this.args=args;
		
	}
	
	
	@Override
	public IRealm<Object> getRealm(IHasMeta meta, Object parent) {
		if (realm==null){
			this.realm=new OrderedRealm<Object>(args);
			this.realm.markReadOnly();
		}
		return realm;
	}
	
	
}
