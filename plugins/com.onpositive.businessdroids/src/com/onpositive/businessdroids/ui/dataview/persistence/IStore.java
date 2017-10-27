package com.onpositive.businessdroids.ui.dataview.persistence;

import android.os.Parcelable;

public interface IStore {

	IStore getSubStore(String key);

	IStore getOrCreateSubStore(String s);

	void putString(String key, String value);

	String getString(String key, String defaultValue);

	void putInt(String key, int value);

	int getInt(String key, int defaultValue);

	void putParcelable(String key, Parcelable parcelable);

	Parcelable getParcelable(String key, Parcelable defaultValue);

	void putBoolean(String key, boolean value);

	boolean getBoolean(String key, boolean defaultValue);

	void putBytes(String key, byte[] data);

	byte[] getBytes(String key);

	void storeObject(String key, Object object);

	Object loadObject(String key);
}
