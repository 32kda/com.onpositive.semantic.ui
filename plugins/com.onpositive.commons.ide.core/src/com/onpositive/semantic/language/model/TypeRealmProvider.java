package com.onpositive.semantic.language.model;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.CompletionProviderRegistry;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.TypeValidatorRegistry;
import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public class TypeRealmProvider implements IRealmProvider<String> {

	public IRealm<String> getRealm(IBinding model) {
		final Object object = model.getObject();
		if (object instanceof ModelElement) {
			final ModelElement el = (ModelElement) object;
			IRealm<String> attributeTypes = el.getModel().getAttributeTypes();
			Realm<String>rr=new Realm<String>(attributeTypes.getContents());
			for (GenericRegistryObject m: CompletionProviderRegistry.getRegistry()){
				rr.add(m.getId());				
			}
			for (GenericRegistryObject m: TypeValidatorRegistry.getRegistry()){
				rr.add(m.getId());				
			}
			return rr;
			
		}
		
		return null;
	}

}
