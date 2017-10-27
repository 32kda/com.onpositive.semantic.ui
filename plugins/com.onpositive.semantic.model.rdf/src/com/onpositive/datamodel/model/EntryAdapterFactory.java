package com.onpositive.datamodel.model;

import org.eclipse.core.runtime.IAdapterFactory;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.api.property.IPropertyProvider;

public class EntryAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertyProvider.class) {
			final IEntry e = (IEntry) adaptableObject;
			return e.getPropertyProvider();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IPropertyProvider.class };
	}

}
