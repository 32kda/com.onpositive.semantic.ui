package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.commons.platform.registry.RegistryMap;

public final class FilterDefinitionRegistry extends
		RegistryMap<FilterDefinitionObject> {

	private static FilterDefinitionRegistry instance;
	private HashMap<String, ArrayList<FilterDefinitionObject>> filters = new HashMap<String, ArrayList<FilterDefinitionObject>>();

	private FilterDefinitionRegistry() {
		super("com.onpositive.semantic.ui.realm.filterDefinition",
				FilterDefinitionObject.class);
	}
	
	public static FilterDefinitionRegistry getInstance(){
		if (instance==null){
			instance=new FilterDefinitionRegistry();
		}
		return instance;
	}
	
	public FilterDefinitionObject[] getDefinedFilters(String viewerId){
		ArrayList<FilterDefinitionObject> arrayList = filters.get(viewerId);
		return arrayList.toArray(new FilterDefinitionObject[arrayList.size()]);
	}

	
	protected void initMap(HashMap<String, FilterDefinitionObject> map) {
		super.initMap(map);
		for (FilterDefinitionObject o : map.values()) {
			String viewerDefinition = o.viewerDefinition();
			ArrayList<FilterDefinitionObject> arrayList = filters
					.get(viewerDefinition);
			if (arrayList == null) {
				arrayList = new ArrayList<FilterDefinitionObject>();
				filters.put(viewerDefinition, arrayList);
			}
		}
	}
}
