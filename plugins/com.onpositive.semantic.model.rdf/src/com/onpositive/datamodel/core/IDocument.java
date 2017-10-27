package com.onpositive.datamodel.core;

import com.onpositive.datamodel.impl.storage.IStorableInByteBuffer;


public interface IDocument extends IDataStore,IStorableInByteBuffer {

	int getVersionNumber();
	
	
}
