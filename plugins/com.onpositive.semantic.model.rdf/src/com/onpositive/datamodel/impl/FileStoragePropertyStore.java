package com.onpositive.datamodel.impl;

import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.storage.FileHeapStorage;
import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.ObjectPool;

public class FileStoragePropertyStore extends AbstractOneValuePropertyStore {

	private FileHeapStorage storage;

	public FileStoragePropertyStore(String property, String path) {
		super(property);
		try {
			storage = new FileHeapStorage(new ObjectPool() {

				
				public int decodeValue(GrowingByteArray arrray, int offset,
						int position, Object[] result) {
					return 0;
				}

				
				public void encodeValue(GrowingByteArray array, Object value) {

				}

				
				public int skipValue(GrowingByteArray array, int offset) {
					return 0;
				}


				
				public Class<?> resolveClass(ObjectStreamClass desc) {
					return null;
				}


				
				public boolean isJavaProperty(String id) {
					return false;
				}

			}, path);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void getEntriesPointingTo(String propId, IEntry value,
			HashSet<IEntry> result) {
		if (propId.equals(property)) {

		}
	}

	public void getEntriesWith(String property, Set<IEntry> toFill) {

	}

	public void getEntriesWithValue(String propId, Object value,
			Set<IEntry> result) {
		if (propId.equals(property)) {

		}
	}

	public Object[] getValues(IEntry e, String property) {
		if (property.equals(this.property)) {
			return storage.getValues(e.getId());
		} else {
			return null;
		}
	}

	public void setValues(IEntry e, String property, Object... values) {
		storage.setValues(e.getId(), values);
	}

	public boolean wantsToStore(String key) {
		return key.equals(property);
	}
}
