package com.onpositive.commons.namespace.ide.ui.core;

import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEExtensionRegistry;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;

public class PDERealmProvider implements IRealmProvider<String>{

	
	public IRealm<String> getRealm(IHasMeta model,Object parent,Object obj) {
		final Object object = parent;		
		PDEExtensionRegistry extensionsRegistry = PDECore.getDefault().getExtensionsRegistry();
		
		return null;
	}

}
