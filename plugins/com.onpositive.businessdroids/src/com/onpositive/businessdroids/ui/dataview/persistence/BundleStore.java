package com.onpositive.businessdroids.ui.dataview.persistence;

import java.io.Serializable;

import android.os.Bundle;
import android.os.Parcelable;

public class BundleStore implements IStore {

	protected final Bundle bundle;

	public BundleStore(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public IStore getSubStore(String key) {
		return new BundleStore(this.bundle.getBundle(key));
	}

	@Override
	public IStore getOrCreateSubStore(String s) {
		Bundle childBundle = this.bundle.getBundle(s);
		if (childBundle == null) {
			childBundle = new Bundle();
			this.bundle.putBundle(s, childBundle);
		}
		return new BundleStore(childBundle);
	}

	@Override
	public void putString(String key, String value) {
		this.bundle.putString(key, value);
	}

	@Override
	public String getString(String key, String defaultValue) {
		String result = this.bundle.getString(key);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	@Override
	public void putInt(String key, int value) {
		this.bundle.putInt(key, value);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		int result = this.bundle.getInt(key, defaultValue);
		return result;
	}

	@Override
	public void putBoolean(String key, boolean value) {
		this.bundle.putBoolean(key, value);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return this.bundle.getBoolean(key, defaultValue);
	}

	@Override
	public void putBytes(String key, byte[] data) {
		this.bundle.putByteArray(key, data);
	}

	@Override
	public byte[] getBytes(String key) {
		return this.bundle.getByteArray(key);
	}

	@Override
	public void storeObject(String key, Object object) {
		if (object instanceof Serializable) {
			this.bundle.putSerializable(key, (Serializable) object);
		} else if (object != null) {
			System.err.println("ALARM!!!");
		}
	}

	@Override
	public Object loadObject(String key) {
		Serializable result = this.bundle.getSerializable(key);
		if (result != null) {
			return result;
		}
		return null;
	}

	@Override
	public void putParcelable(String key, Parcelable parcelable) {
		this.bundle.putParcelable(key, parcelable);
	}

	@Override
	public Parcelable getParcelable(String key, Parcelable defaultValue) {
		Parcelable parcelable = this.bundle.getParcelable(key);
		if (parcelable == null) {
			return defaultValue;
		}
		return parcelable;
	}

}
