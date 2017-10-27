package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;


public class PropertiesProperty extends ComputedProperty{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public PropertiesProperty(String id, String name) {
		super(id, name,true,false,IProperty.class,null);
		((BaseMeta)getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, IProperty.class);
	}
	
	public static final PropertiesProperty INSTANCE=new PropertiesProperty("@properties", "Properties"); 

	
	@Override
	public Object getValue(Object obj) {
		return PropertyAccess.getProperties(obj);
	}

}
