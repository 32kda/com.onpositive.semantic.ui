package com.onpositive.semantic.model.api.id;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IService;


public interface IIdentifierProvider<T> extends IService{

	public T getObject(IHasMeta meta, Object parent, Object id);

	public Object getId(IHasMeta meta, Object parent, Object object2);
}
