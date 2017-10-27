package com.onpositive.semantic.model.api.globals;

import com.onpositive.semantic.model.api.meta.IService;

public interface IKeyResolver  extends IService{

	Object resolveKey(String key);

	IKey   getKey(Object obj);
}
