package com.onpositive.commons.namespace.ide.ui.core;

import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;

public class ElementRealmProvider implements IRealmProvider<String> {


	public IRealm<String> getRealm(IHasMeta model, Object parentObject,
			Object object1) {
		final Object object = parentObject;
		if (object instanceof ModelElement) {
			final ModelElement el = (ModelElement) object;
			return el.getModel().getElementNames();
		}
		return null;
	}

}
