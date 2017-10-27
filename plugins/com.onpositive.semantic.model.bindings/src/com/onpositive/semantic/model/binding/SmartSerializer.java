package com.onpositive.semantic.model.binding;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class SmartSerializer {

	public static class SerializationRecord {
		Class<?> clazz;
		Field[] f;
		ArrayList<FieldSerializer>tm=new ArrayList<FieldSerializer>();
		
		SerializationRecord superRecord;

		public SerializationRecord(Class<?> clazz) {
			super();
			this.clazz = clazz;
			rs.put(clazz, this);
			this.f = clazz.getDeclaredFields();
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				superRecord=record(superclass);
			}
			for (Field f:this.f){
				int modifiers = f.getModifiers();
				if (Modifier.isStatic(modifiers)){
					continue;
				}
				if (Modifier.isTransient(modifiers)){
					continue;
				}
//				if (f.getAnnotation(DefaultSerialize.class)!=null){
//					continue;
//				}
				if (!ts.containsKey(f.getType())){
					tm.add(new FieldSerializer(f,null));
				}
				else{
					tm.add(new FieldSerializer(f,ts.get(f.getType())));
				}
				
			}
		}

		public void write(Object object, DataOutputStream ds) throws Exception {
			for (FieldSerializer f:tm){
				f.serialize(object, ds);
			}
		}
	}
	static abstract class TypeSerializer{
		
		public abstract void serialize(Object object,DataOutputStream d) throws Exception;
		public abstract Object deserialize(Object object,DataInputStream d) throws Exception;
	}
	
	static class FieldSerializer{
		
		protected final Field f;
		
		protected final TypeSerializer ts;
		
		public FieldSerializer(Field f, TypeSerializer ts) {
			super();
			this.f = f;
			this.ts = ts;
			f.setAccessible(true);
		}

		public void serialize(Object object,DataOutputStream d) throws Exception{
			Object object2 = f.get(object);
			if (object2!=null){
				d.writeUTF(f.getName());
				if (ts!=null){
					ts.serialize(object2, d);
				}
				else
				{
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ObjectOutputStream o=new ObjectOutputStream(byteArrayOutputStream);
					o.writeObject(object2);
					o.close();
					byte[] byteArray = byteArrayOutputStream.toByteArray();
					d.writeInt(byteArray.length);
					d.write(byteArray);
				}				
				System.out.println(f.getName()+":"+f.getType().getName());
			}
		}
		
	}
	
	static HashMap<Class, TypeSerializer>ts=new HashMap<Class, TypeSerializer>();
	
	static{
		TypeSerializer typeSerializer = new TypeSerializer(){

			@Override
			public void serialize(Object object, DataOutputStream d) throws IOException {
				if (object==null){
					d.writeByte(0);
				}
				Boolean b=(Boolean) object;
				if (b){
					d.writeByte(1);
				}
				else{
					d.writeByte(2);
				}
			}

			@Override
			public Object deserialize(Object object, DataInputStream d) throws IOException {
				byte readByte = d.readByte();
				if (readByte!=0){
					if (readByte==1){
						return Boolean.TRUE;
					}
					return Boolean.FALSE;
				}
				return null;
			}
			
		};
		ts.put(Boolean.class, typeSerializer);
		ts.put(boolean.class, typeSerializer);
		TypeSerializer typeSerializer2 = new TypeSerializer(){

			@Override
			public void serialize(Object object, DataOutputStream d) throws IOException {
				d.writeInt((Integer)object);
			}

			@Override
			public Object deserialize(Object object, DataInputStream d) throws IOException {
				return d.readInt();
			}
			
		};
		ts.put(int.class, typeSerializer2);
		ts.put(Integer.class, typeSerializer2);
		ts.put(String.class, new TypeSerializer(){

			@Override
			public void serialize(Object object, DataOutputStream d) throws IOException {
				d.writeUTF(object.toString());
			}

			@Override
			public Object deserialize(Object object, DataInputStream d) throws IOException {
				return d.readUTF();
			}
			
		});
//		ts.put(CodeAndMessage.class, new TypeSerializer(){
//
//			@Override
//			public void serialize(Object object, DataOutputStream d)
//					throws Exception {
//				
//			}
//
//			@Override
//			public Object deserialize(Object object, DataInputStream d)
//					throws Exception {
//				return null;
//			}
//			
//		});
		ts.put(Class.class, new TypeSerializer(){

			@Override
			public void serialize(Object object, DataOutputStream d) throws IOException {
				d.writeUTF(((Class)object).getName());
			}

			@Override
			public Object deserialize(Object object, DataInputStream d) throws ClassNotFoundException, IOException {
				return Class.forName( d.readUTF());
			}
			
		});
	}

	static HashMap<Class, SerializationRecord> rs = new HashMap<Class, SmartSerializer.SerializationRecord>();
	
	static SerializationRecord record(Class<?>cl){
		SerializationRecord serializationRecord = rs.get(cl);
		if (serializationRecord==null){
			serializationRecord=new SerializationRecord(cl);
			
		}
		return serializationRecord;
	}

	public static void serialize(Object object, Class<?> clazz) {
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		DataOutputStream ds=new DataOutputStream(os);
		SerializationRecord record = record(clazz);
		try{
		record.write(object,ds);
		System.out.println(os.size());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
