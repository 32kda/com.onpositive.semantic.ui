package com.onpositive.datamodel.impl.storage;

public interface IStorableInByteBuffer {

	void store(GrowingByteArray ba);

	int load(GrowingByteArray array, int position);
}
