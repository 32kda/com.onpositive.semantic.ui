package com.onpositive.semantic.model.api.access;

import com.onpositive.semantic.model.api.meta.IService;

public interface IExternalizer extends IService{

	String externalizeMessage(String message);
}
