package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IService;


public interface IRealmProvider<T> extends IService{

	/**
	 * 
	 * @param model
	 * @param parentObject TODO
	 * @param object TODO
	 * @return realm of values that may correspond to a given element of model
	 */
	IRealm<T> getRealm(IHasMeta model, Object parentObject, Object object);

}
