package com.onpositive.datamodel.impl;

public class StorageConfiguration {

	private String name;
	private String provider;
	private String url;
	private long synctimeout;

	public String getProvider() {
		return this.provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLocalId() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSynctimeout() {
		return this.synctimeout;
	}

	public void setSynctimeout(long synctimeout) {
		this.synctimeout = synctimeout;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.provider == null) ? 0 : this.provider.hashCode());
		result = prime * result + (int) (this.synctimeout ^ (this.synctimeout >>> 32));
		result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
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
		final StorageConfiguration other = (StorageConfiguration) obj;
		if (this.provider == null) {
			if (other.provider != null) {
				return false;
			}
		} else if (!this.provider.equals(other.provider)) {
			return false;
		}
		if (this.synctimeout != other.synctimeout) {
			return false;
		}
		if (this.url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!this.url.equals(other.url)) {
			return false;
		}
		return true;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}