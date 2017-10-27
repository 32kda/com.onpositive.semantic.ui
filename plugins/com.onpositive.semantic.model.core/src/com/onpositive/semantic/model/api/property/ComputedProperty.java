package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.validation.IValidationContext;

public abstract class ComputedProperty extends DefaultProperty {

	private static final long serialVersionUID = 1L;
	public ComputedProperty(String id, String name, boolean multivalue,
			boolean allowGroup, Class<?> subjectClass, IPropertyProvider lookup) {
		super(id, new BaseMeta());
		BaseMeta m = (BaseMeta) getMeta();
		m.putMeta(DefaultMetaKeys.PROP_ID_KEY, id);
		m.putMeta(DefaultMetaKeys.CAPTION_KEY, name);
		m.putMeta(DefaultMetaKeys.MULTI_VALUE_KEY, multivalue);
		m.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
		m.putMeta(DefaultMetaKeys.GROUP_KEY, true);
		m.putMeta(DefaultMetaKeys.COMPUTED_KEY, true);		
		m.putMeta(IValidationContext.DEEP_VALIDATION,false);
	}
	public ComputedProperty(String id, String name,Class<?>sClass)
	{
		this(id,name,false,true,sClass,null);
	}
	public ComputedProperty(String id, String name)
	{
		this(id,name,false,true,Object.class,null);
	}
	public ComputedProperty()
	{
		this("","",false,true,Object.class,null);
	}

}
