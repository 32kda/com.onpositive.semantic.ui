package com.onpositive.commons.namespace.ide.ui.core;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;

public class GroupRealmProvider implements IRealmProvider<String> {

	
	public IRealm<String> getRealm(IHasMeta model,Object parent,Object obj) {
		return GroupRegistry.getRegistry().getGroups();
	}

}
