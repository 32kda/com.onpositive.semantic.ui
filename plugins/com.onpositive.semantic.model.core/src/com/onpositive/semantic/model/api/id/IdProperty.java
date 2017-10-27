package com.onpositive.semantic.model.api.id;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.realm.IRealm;

public class IdProperty extends ComputedProperty{

	public IdProperty(String id, String name) {
		super(id, name);
		((BaseMeta)getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, Object.class);
	}
	
	public static final IdProperty INSTANCE=new IdProperty("@id", "id");

	
	@Override
	public Object getValue(Object obj) {
		return IdAccess.getId(obj);
	}

}
