package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.IContextDependingProperty;
import com.onpositive.semantic.model.api.realm.IRealm;

public class LabelProperty extends ComputedProperty implements IContextDependingProperty{

	private static final long serialVersionUID = 1L;


	public LabelProperty(String id, String name) {
		super(id, name); 
		((BaseMeta)getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, String.class);
	}
	
	public static final LabelProperty INSTANCE=new LabelProperty("@label", "Label");

	
	@Override
	public Object getValue(Object obj) {		
		return LabelAccess.getLabel(obj);
	}
	
	
	@Override
	public Object getValue(IHasMeta parentProp,Object parent,Object object){
		return LabelAccess.getLabel(parentProp,parent,object);		
	}
}
