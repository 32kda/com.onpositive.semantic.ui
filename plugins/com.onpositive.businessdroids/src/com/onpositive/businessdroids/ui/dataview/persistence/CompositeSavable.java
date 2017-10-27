package com.onpositive.businessdroids.ui.dataview.persistence;

import java.util.LinkedHashMap;

public class CompositeSavable implements ISaveable {

	protected LinkedHashMap<String, ISaveable> parts = new LinkedHashMap<String, ISaveable>();

	@Override
	public void save(IStore store) {
		for (String s : this.parts.keySet()) {
			IStore subStore = store.getOrCreateSubStore(s);
			if (subStore != null) {
				this.parts.get(s).save(store);
			}
		}
	}

	@Override
	public void load(IStore store) {
		for (String s : this.parts.keySet()) {
			IStore subStore = store.getSubStore(s);
			if (subStore != null) {
				try {
					this.parts.get(s).load(store);
				} catch (NoSuchElement e) {
				}
			}
		}
	}

	public ISaveable put(String key, ISaveable value) {
		return this.parts.put(key, value);
	}

	public ISaveable remove(String key) {
		return this.parts.remove(key);
	}

}
