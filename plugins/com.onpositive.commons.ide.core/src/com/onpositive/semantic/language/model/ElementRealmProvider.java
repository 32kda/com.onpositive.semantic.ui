package com.onpositive.semantic.language.model;

import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;

public class ElementRealmProvider implements IRealmProvider<String> {

	public IRealm<String> getRealm(IBinding model) {
		final Object object = model.getObject();
		if (object instanceof ModelElement) {
			final ModelElement el = (ModelElement) object;
			return el.getModel().getElementNames();
		}
		return null;
	}

}
