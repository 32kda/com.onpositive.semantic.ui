package com.onpositive.semantic.model.entity.appengine;

import java.io.Serializable;

import com.onpositive.semantic.model.api.meta.IMeta;

public interface IVariableResolver extends Serializable{

	Object resolveObject(String key, IMeta meta);
}
