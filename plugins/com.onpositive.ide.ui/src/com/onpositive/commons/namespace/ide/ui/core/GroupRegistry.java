package com.onpositive.commons.namespace.ide.ui.core;

import java.util.HashMap;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.semantic.model.api.realm.Realm;

public class GroupRegistry extends RegistryMap<GenericRegistryObject> {

	private GroupRegistry() {
		super("com.onpositive.ide.ui.group", GenericRegistryObject.class);
	}

	private static GroupRegistry registry;

	Realm<String> groups = new Realm<String>();

	public static GroupRegistry getRegistry() {
		if (registry == null) {
			registry = new GroupRegistry();
		}
		return registry;
	}

	public Realm<String> getGroups() {
		this.checkLoad();
		return this.groups;
	}

	
	protected void initMap(HashMap<String, GenericRegistryObject> map) {
		super.initMap(map);
		this.groups = new Realm<String>(map.keySet());
	}

}
