package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.IContextDependingProperty;

public class RealmProperty extends ComputedProperty implements IContextDependingProperty{

	private static final long serialVersionUID = 1L;


	public RealmProperty(String id, String name) {
		super(id, name); 
		((BaseMeta)getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, IRealm.class);
	}
	
	public static final RealmProperty INSTANCE=new RealmProperty("@realm", "Realm");

	
	@Override
	public Object getValue(Object obj) {		
		return RealmAccess.getRealm(obj);
	}
	
	
	@Override
	public Object getValue(IHasMeta parentProp,Object parent,Object object){
		return RealmAccess.getRealm(parentProp,parent,object);		
	}
}
