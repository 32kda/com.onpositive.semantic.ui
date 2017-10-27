package com.onpositive.datamodel.impl.storage;

import java.io.ObjectStreamClass;

public abstract class ObjectPool {

	public abstract int decodeValue(GrowingByteArray arrray, int offset,
			int position, Object[] result);

	public abstract void encodeValue(GrowingByteArray array, Object value);

	public abstract int skipValue(GrowingByteArray array, int offset);

	public abstract Class<?> resolveClass(ObjectStreamClass desc);

	public abstract boolean isJavaProperty(String id);

}
