package com.onpositive.semantic.model.data;

import java.util.Set;

import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.realm.IIdentifiableObject;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.ITypedObject;

public interface IEntry extends ITypedObject,IIdentifiableObject{

	IPropertyProvider getPropertyProvider();

	Set<IType> getTypes();

}
