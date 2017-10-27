package com.onpositive.semantic.model.api.changes;

import com.onpositive.semantic.model.api.meta.IService;


public interface IObjectListenersProvider extends IService{

	IObjectListener[] getListeners(Object obj, String role);
	
}
