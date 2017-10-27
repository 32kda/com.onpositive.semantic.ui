package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface IContextDependingProperty extends IProperty{

	public Object getValue(IHasMeta parentMeta,Object parentValue,Object value);
}
