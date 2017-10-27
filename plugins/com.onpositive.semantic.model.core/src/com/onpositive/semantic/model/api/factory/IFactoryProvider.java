package com.onpositive.semantic.model.api.factory;

import com.onpositive.semantic.model.api.meta.IService;
import com.onpositive.semantic.model.api.property.IFunction;

public interface IFactoryProvider extends IService{

	IFunction getElementFactory(Object model);
}
