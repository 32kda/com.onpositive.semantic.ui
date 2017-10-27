package com.onpositive.datamodel.impl.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

public class AbstractValueTable {

	protected static final byte FALSE = 0;
	protected static final byte TRUE = 1;
	protected static final byte INTEGER = 2;
	protected static final byte LONG = 3;
	protected static final byte DOUBLE = 4;
	protected static final byte STRING = 5;
	protected static final byte DATE = 6;
	protected static final byte SERIALIZABLE = 8;
	public static final byte CUSTOM = 7;
	protected static final Object[] NO_VALUES = new Object[0];
	protected static StringBuilder bld = new StringBuilder();
	final ObjectPool pool;
	
	public AbstractValueTable(ObjectPool pool) {
		this.pool=pool;
	}
	
	protected void writeValues(GrowingByteArray array2, Object... values) {
		array2.add(values.length);
		final int pos = array2.getSize();
		array2.add(Integer.MAX_VALUE);
		for (int a = 0; a < values.length; a++) {
			this.encodeValue(values[a], array2);
		}
		final int vl = array2.getSize() - pos - 4;
		array2.set(pos, vl);
	}

	protected void encodeValue(Object object, GrowingByteArray array2) {
		final Class<?> oclazz = object.getClass();		
		if (oclazz == Boolean.class) {
			final Boolean b = (Boolean) object;
			if (!b) {
				array2.add(FALSE);
			} else {
				array2.add(TRUE);
			}
		} else if (oclazz == Integer.class) {
			array2.add(INTEGER);
			final Integer intv = (Integer) object;
			array2.add(intv);
		} else if (oclazz == Date.class) {
			array2.add(DATE);
			final Date intv = (Date) object;
			array2.add(intv.getTime());
		} else if (oclazz == Long.class) {
			array2.add(LONG);
			final Long intv = (Long) object;
			array2.add(intv.longValue());
		} else if (oclazz == Double.class) {
			array2.add(DOUBLE);
			final Double intv = (Double) object;
			array2.add(Double.doubleToRawLongBits(intv));
		} else if (oclazz == String.class) {
			array2.add(STRING);
			final String intv = (String) object;
			array2.add(intv);
		}
		else if (object instanceof Serializable) {
			array2.add(SERIALIZABLE);
			try{
			ByteArrayOutputStream ba=new ByteArrayOutputStream();
			ObjectOutputStream z=new ObjectOutputStream(ba);
			z.writeObject(object);
			z.close();
			byte[] byteArray = ba.toByteArray();
			array2.add(byteArray.length);
			for (int a=0;a<byteArray.length;a++){
				array2.add(byteArray[a]);
			}
			}catch (IOException e) {
				throw new RuntimeException();
			}			
		}
		else {
			array2.add(CUSTOM);
			this.pool.encodeValue(array2, object);
		}

	}
	
	protected int decodeValue(int offset, int a, Object[] result,
			GrowingByteArray array2) {
		final byte type = array2.bytes[offset];
		offset += 1;
		switch (type) {
		case CUSTOM:
			return this.pool.decodeValue(array2, offset, a, result);
		case FALSE:
			result[a] = Boolean.FALSE;
			// BOOLEAN TRUE
			break;
		case TRUE:
			result[a] = Boolean.TRUE;
			// BOOLEAN TRUE
			break;
		case INTEGER: {
			result[a] = array2.getInt(offset);
			offset += 4;
			// INTEGER
			break;
		}
		case LONG: {
			final byte vl0 = array2.bytes[offset++];
			final byte vl1 = array2.bytes[offset++];
			final byte vl2 = array2.bytes[offset++];
			final byte vl3 = array2.bytes[offset++];
			final byte vl4 = array2.bytes[offset++];
			final byte vl5 = array2.bytes[offset++];
			final byte vl6 = array2.bytes[offset++];
			final byte vl7 = array2.bytes[offset++];
			result[a] = BitUtils.makeLong(vl7, vl6, vl5, vl4, vl3, vl2, vl1,
					vl0);
			// LONG
			break;
		}
		case DATE: {
			final byte vl0 = array2.bytes[offset++];
			final byte vl1 = array2.bytes[offset++];
			final byte vl2 = array2.bytes[offset++];
			final byte vl3 = array2.bytes[offset++];
			final byte vl4 = array2.bytes[offset++];
			final byte vl5 = array2.bytes[offset++];
			final byte vl6 = array2.bytes[offset++];
			final byte vl7 = array2.bytes[offset++];
			result[a] = new Date(BitUtils.makeLong(vl7, vl6, vl5, vl4, vl3,
					vl2, vl1, vl0));
			// LONG
			break;
		}

		case DOUBLE: {
			// DOUBLE
			final byte vl0 = array2.bytes[offset++];
			final byte vl1 = array2.bytes[offset++];
			final byte vl2 = array2.bytes[offset++];
			final byte vl3 = array2.bytes[offset++];
			final byte vl4 = array2.bytes[offset++];
			final byte vl5 = array2.bytes[offset++];
			final byte vl6 = array2.bytes[offset++];
			final byte vl7 = array2.bytes[offset++];
			result[a] = Double.longBitsToDouble(BitUtils.makeLong(vl7, vl6,
					vl5, vl4, vl3, vl2, vl1, vl0));
			break;
		}
		case SERIALIZABLE:
			int ba=array2.getInt(offset);
			byte[] resultA=new byte[ba];
			offset+=4;
			int i=0;
			for (int b=offset;b<offset+ba;b++){
				resultA[i++]=array2.bytes[b];
			}
			try{
			ObjectInputStream is=new ObjectInputStream(new ByteArrayInputStream(resultA)){

				
				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					// TODO Auto-generated method stub
					
					return pool.resolveClass(desc);
				}
				
			};			
			Object readObject = is.readObject();
			result[a]=readObject;
			is.close();
			return offset+ba;
			}catch (IOException e) {
				//throw new RuntimeException(e);
				return offset+ba;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		case STRING:
			// STRING
		{
			synchronized (bld) {
				offset = array2.readString(offset, bld);			
				result[a] = bld.toString();
				bld.delete(0, bld.length());	
			}			
			break;
		}
		default:
			break;
		}
		return offset;
	}
}
