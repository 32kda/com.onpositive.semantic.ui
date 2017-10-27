package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.semantic.realm.IColumnDefinition;

public final class ColumnDefinitionRegistry extends
		RegistryMap<ColumnDefinitionObject> {

	private static ColumnDefinitionRegistry instance;
	private HashMap<String, ArrayList<ColumnDefinitionObject>> columns = new HashMap<String, ArrayList<ColumnDefinitionObject>>();

	private ColumnDefinitionRegistry() {
		super("com.onpositive.semantic.ui.realm.columnDefinition",
				ColumnDefinitionObject.class);
	}

	public static ColumnDefinitionRegistry getInstance() {
		if (instance == null) {
			instance = new ColumnDefinitionRegistry();
		}
		return instance;
	}

	public IColumnDefinition[] getDefinedColumns(String viewerId) {
		checkLoad();
		ArrayList<ColumnDefinitionObject> arrayList = columns.get(viewerId);
		Collections.sort(arrayList,new Comparator<IColumnDefinition>(){

			
			public int compare(IColumnDefinition o1, IColumnDefinition o2) {
				return o1.name().compareTo(o2.name());
			}
			
		});
		return arrayList.toArray(new IColumnDefinition[arrayList.size()]);
	}

	
	protected void initMap(HashMap<String, ColumnDefinitionObject> map) {
		super.initMap(map);
		for (ColumnDefinitionObject o : map.values()) {
			String viewerDefinition = o.viewerDefinition();
			ArrayList<ColumnDefinitionObject> arrayList = columns
					.get(viewerDefinition);
			if (arrayList == null) {
				arrayList = new ArrayList<ColumnDefinitionObject>();
				columns.put(viewerDefinition, arrayList);
			}
			arrayList.add(o);
		}
	}

}
