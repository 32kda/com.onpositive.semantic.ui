package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.realm.IColumnDefinition;

public class AvailableColumnsProvider implements IRealmProvider<ColumnConfiguration>{

	
	
	public IRealm<ColumnConfiguration> getRealm(IBinding model) {
		ArrayList<ColumnConfiguration>columns=new ArrayList<ColumnConfiguration>();
		ViewerTabConfiguration object = (ViewerTabConfiguration) model.getObject();
		IColumnDefinition[] allPossibleColumns = object.getOwner().getDefinition().allPossibleColumns();
		for (IColumnDefinition a:allPossibleColumns){
			ColumnDefinitionObject obj=(ColumnDefinitionObject) a;
			columns.add(obj.createColumnConfiguration());
		}
		return new Realm<ColumnConfiguration>(columns);
	}

}
