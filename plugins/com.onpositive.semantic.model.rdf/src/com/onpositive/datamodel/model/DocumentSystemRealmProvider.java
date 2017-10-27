package com.onpositive.datamodel.model;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.registry.IUrlDependingRealmProvider;

public class DocumentSystemRealmProvider implements
		IUrlDependingRealmProvider<IEntry> {

	public IRealm<?> getRealm(String url) {
		final int sm = url.lastIndexOf('#');
		if (sm != -1) {
			final ConfigurableDocumentSystem system = DocumentSystemRegistry
					.getInstance().getSystem(url.substring(0, sm));
			final IDataStoreRealm realm = system.getRealm();
			final String typeName = url.substring(sm + 1);
			final IType type = realm.getType(typeName);
			return realm.getTypeRealm(type);
		} else {
			final ConfigurableDocumentSystem system = DocumentSystemRegistry
					.getInstance().getSystem(url);
			return system.getRealm();
		}
	}
}
