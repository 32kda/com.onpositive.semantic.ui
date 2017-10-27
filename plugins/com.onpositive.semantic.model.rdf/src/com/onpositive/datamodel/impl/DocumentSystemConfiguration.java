package com.onpositive.datamodel.impl;

import java.util.ArrayList;

public class DocumentSystemConfiguration {

	private String name;

	private boolean inited;

	private ArrayList<StorageConfiguration> storages = new ArrayList<StorageConfiguration>();

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<StorageConfiguration> getStorages() {
		return this.storages;
	}

	public void setStorages(ArrayList<StorageConfiguration> storages) {
		this.storages = storages;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.storages == null) ? 0 : this.storages.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final DocumentSystemConfiguration other = (DocumentSystemConfiguration) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.storages == null) {
			if (other.storages != null) {
				return false;
			}
		} else if (!this.storages.equals(other.storages)) {
			return false;
		}
		return true;
	}

	public boolean isInited() {
		return this.inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	public void removeStorage(StorageConfiguration storage) {
		this.storages.remove(storage);
	}

	public void addStorage(StorageConfiguration storage) {
		this.storages.add(storage);
	}
}
