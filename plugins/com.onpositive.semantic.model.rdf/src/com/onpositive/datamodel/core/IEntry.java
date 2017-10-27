package com.onpositive.datamodel.core;

import java.util.Set;

import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.realm.IIdentifiableObject;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.ITypedObject;

public interface IEntry extends IIdentifiableObject, ITypedObject {

	String getId();

	String getName();

	IRealm<IEntry> getRealm();

	IPropertyProvider getPropertyProvider();

	Set<IType> getTypes();

	Set<Object> getValues(String propName);

	Object 		getValue(String propName);

	boolean isInstance(IType type);
}
