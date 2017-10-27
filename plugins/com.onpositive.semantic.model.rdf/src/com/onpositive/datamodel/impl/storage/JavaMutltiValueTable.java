package com.onpositive.datamodel.impl.storage;

import com.onpositive.datamodel.core.StorableObject;

public class JavaMutltiValueTable extends MultiValueTable{

	public JavaMutltiValueTable(ObjectPool pool) {
		super(pool);
	}
	
	
	public Object getValue(int id) {
		Object value = super.getValue(id);
		if (value instanceof StorableObject){
			StorableObject o=(StorableObject) value;
			return o.getOriginalObject();
		}
		return value;
	}

	
	public Object[] getValues(int handle) {
		Object[] values = super.getValues(handle);
		Object[] obj=new Object[values.length];
		for (int a=0;a<values.length;a++){
			Object obj2 = values[a];
			if (obj2 instanceof StorableObject){
				StorableObject st=(StorableObject) obj2;
				obj2=st.getOriginalObject();
			}
			obj[a]=obj2;
		}
		return obj;
	}

	public void setValues(int handle, Object... values) {
		Object[] obj=new Object[values.length];
		for (int a=0;a<values.length;a++){
			obj[a]=new StorableObject(values[a],pool);
		}
		super.setValues(handle, obj);
	}
	
	protected void encodeValue(Object object, GrowingByteArray array2) {
		if (object instanceof StorableObject){
			array2.add(SERIALIZABLE);
			StorableObject s=(StorableObject) object;
			byte[] byteArray = s.getBytes();
			array2.add(byteArray.length);
			for (int a=0;a<byteArray.length;a++){
				array2.add(byteArray[a]);
			}			
			return;
		}
		super.encodeValue(object, array2);
	}
		
}
