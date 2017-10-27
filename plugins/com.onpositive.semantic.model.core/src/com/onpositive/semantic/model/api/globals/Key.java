package com.onpositive.semantic.model.api.globals;

import java.io.Serializable;

public class Key implements IKey, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final IKey parentKey;
	private final String localIdLong;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((localIdLong == null) ? 0 : localIdLong.hashCode());
		result = prime * result
				+ ((parentKey == null) ? 0 : parentKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (localIdLong == null) {
			if (other.localIdLong != null)
				return false;
		} else if (!localIdLong.equals(other.localIdLong))
			return false;
		if (parentKey == null) {
			if (other.parentKey != null)
				return false;
		} else if (!parentKey.equals(other.parentKey))
			return false;
		return true;
	}

	public Key(IKey parentKey, String localId) {
		super();
		this.parentKey = parentKey;
		this.localIdLong = localId;
	}

	@Override
	public IKey getParent() {
		return parentKey;
	}

	@Override
	public String getLocalId() {
		return localIdLong;
	}

	public Key append(String localid) {
		return new Key(this, localid);
	}

	@Override
	public String toString() {
		return GlobalAccess.keyToString(this);
	}
}