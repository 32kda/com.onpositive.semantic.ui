package com.onpositive.semantic.model.api.status;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public class StatusProperty extends ComputedProperty{

	public StatusProperty(String id, String name) {
		super(id, name); 
		((BaseMeta)getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, CodeAndMessage.class);
	}
	
	public static final StatusProperty INSTANCE=new StatusProperty("@status", "Status");

	
	@Override
	public Object getValue(Object obj) {
		if (obj instanceof IHasStatus){
			IHasStatus st=(IHasStatus) obj;
			return st.getStatus();	
		}
		return ValidationAccess.validate(obj);
		
	}

	public static CodeAndMessage getStatus(Object obj){
		return (CodeAndMessage) INSTANCE.getValue(obj);		
	}
}
