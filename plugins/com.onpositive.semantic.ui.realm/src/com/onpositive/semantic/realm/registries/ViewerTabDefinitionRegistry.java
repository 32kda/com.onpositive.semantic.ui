package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.commons.platform.registry.RegistryMap;

public class ViewerTabDefinitionRegistry extends
		RegistryMap<ViewerTabDefinition> {

	private static ViewerTabDefinitionRegistry instance;

	private HashMap<String, ArrayList<ViewerTabDefinition>> tabs = new HashMap<String, ArrayList<ViewerTabDefinition>>();

	private ViewerTabDefinitionRegistry() {
		super("com.onpositive.semantic.ui.realm.viewerTabDefinition", ViewerTabDefinition.class);
	}

	public static ViewerTabDefinitionRegistry getInstance() {
		if (instance == null) {
			instance = new ViewerTabDefinitionRegistry();
		}
		return instance;
	}

	public ViewerTabDefinition[] getDefinedTabs(String viewerId) {
		checkLoad();
		ArrayList<ViewerTabDefinition> arrayList = tabs.get(viewerId);
		return arrayList.toArray(new ViewerTabDefinition[arrayList.size()]);
	}

	
	protected void initMap(HashMap<String, ViewerTabDefinition> map) {
		super.initMap(map);
		for (ViewerTabDefinition o : map.values()) {
			String viewerDefinition = o.viewerDefinition();
			ArrayList<ViewerTabDefinition> arrayList = tabs
					.get(viewerDefinition);
			if (arrayList == null) {
				arrayList = new ArrayList<ViewerTabDefinition>();
				tabs.put(viewerDefinition, arrayList);
			}
			arrayList.add(o);
		}
	}
}
