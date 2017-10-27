package com.onpositive.datamodel.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import com.onpositive.datamodel.impl.storage.ObjectPool;


public final class StorableObject{

	byte[] value;
	private ObjectPool pool;
	public StorableObject(Object obj,ObjectPool pool){
		try{
		this.pool=pool;
		ByteArrayOutputStream bas=new ByteArrayOutputStream();
		ObjectOutputStream as=new ObjectOutputStream(bas);
		as.writeObject(obj);
		as.close();
		bas.close();
		value=bas.toByteArray();
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object getOriginalObject(){
		try{
		ByteArrayInputStream is=new ByteArrayInputStream(value);
		ObjectInputStream ios=new ObjectInputStream(is){

			
			protected Class<?> resolveClass(ObjectStreamClass desc)
					throws IOException, ClassNotFoundException {
				return pool.resolveClass(desc);
			}
			
			
		};
		return ios.readObject();
		}catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}		
	}

	public byte[] getBytes() {
		return value;
	}
}
