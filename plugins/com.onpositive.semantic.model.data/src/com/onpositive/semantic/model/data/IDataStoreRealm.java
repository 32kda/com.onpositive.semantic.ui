package com.onpositive.semantic.model.data;

import java.util.Set;

import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.ITypedRealm;

public interface IDataStoreRealm extends IPropertyCalculator{

	void execute(ICommand executableCommand);

	ITypedRealm<IEntry> getTypeRealm(String value);

	ITypedRealm<IEntry> getTypeRealm(ValueClass vClass);

	Set findEntries(String of, IEntry obj);

	void fireDelta(HashDelta<?> dlt);

	IPropertyProvider getPropertyProvider();

	ICommand getObjectDeletionCommand(IEntry entry);

	IEntry newObject(String value);
}
