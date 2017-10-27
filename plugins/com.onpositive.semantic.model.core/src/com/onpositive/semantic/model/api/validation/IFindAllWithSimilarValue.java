package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IService;
import com.onpositive.semantic.model.api.property.IProperty;

public interface IFindAllWithSimilarValue extends IService{

	Iterable<Object>find(IHasMeta meta,Object object,Object value, IProperty prop);
}
