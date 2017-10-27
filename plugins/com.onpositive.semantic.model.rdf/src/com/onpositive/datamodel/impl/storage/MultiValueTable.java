package com.onpositive.datamodel.impl.storage;

import java.util.Collection;
import java.util.HashMap;

import com.onpositive.datamodel.impl.storage.PositiveIntToIntHashTable.Callback;

public class MultiValueTable extends AbstractValueTable implements IStorableInByteBuffer {

	public GrowingByteArray array;	
	
	protected PositiveIntToIntHashTable table;
	
	public HashMap<Integer, Object[]> editedEntries = new HashMap<Integer, Object[]>();

	public void traverse(Callback cb) {
		this.table.traverse(cb);
	}

	public MultiValueTable(ObjectPool pool) {
		super(pool);
		this.array = new GrowingByteArray(100);
		this.table = new PositiveIntToIntHashTable(100);		
	}

	public int getValueCount(int handle) {
		final int i = this.table.get(handle) - 1;
		if (i == -1) {
			return 0;
		}
		final int int1 = this.array.getInt(i);
		if (int1 < 0) {
			final Object[] objects = this.editedEntries.get(i);
			return objects.length;
		}
		return int1;
	}

	public void setValues(int handle, Collection<?> values) {
		this.setValues(handle, values.toArray());
	}

	public void setValues(int handle, Object... values) {
		final int i = this.table.get(handle) - 1;
		final GrowingByteArray array2 = this.array;
		if (i != -1) {
			final int int1 = array2.getInt(i);
			if (int1 < 0) {
				this.editedEntries.put(i, values);
				return;
			} else {
				array2.set(i, -1);
				this.editedEntries.put(i, values);
				return;
			}
		}
		if ((values == null) || (values.length == 0)) {
			this.table.delete(handle);
			return;
		}
		final int size = array2.getSize();
		this.table.put(handle, size + 1);
		this.writeValues(array2, values);
	}

	
	public Object[] getValues(int handle) {
		int i = this.table.get(handle) - 1;
		if (i == -1) {
			return NO_VALUES;
		}
		final int length = this.array.getInt(i);
		if (length < 0) {
			final Object[] objects = this.editedEntries.get(i);
			return objects;
		}
		final Object[] result = new Object[length];
		i += 8;
		for (int a = 0; a < length; a++) {
			i = this.decodeValue(i, a, result);
		}
		return result;
	}

	public int skipValue(int offset) {
		final byte type = this.array.bytes[offset];
		offset += 1;
		switch (type) {
		case CUSTOM:
			return this.pool.skipValue(this.array, offset);
		case FALSE:
			// BOOLEAN TRUE
			break;
		case TRUE:
			// BOOLEAN TRUE
			break;
		case INTEGER: {
			offset += 4;
			// INTEGER
			break;
		}
		case LONG: {
			offset += 8;
			// LONG
			break;
		}
		case DATE: {
			offset += 8;
			// LONG
			break;
		}

		case DOUBLE: {
			// DOUBLE
			offset += 8;
			break;
		}
		case STRING:
			// STRING
		{
			offset = this.array.skipString(offset);
			break;
		}
		case SERIALIZABLE:
			// STRING
		{
			int pz=this.array.getInt(offset);			
			offset = offset+4+pz;
			break;
		}
		default:
			break;
		}
		return offset;
	}

	protected int decodeValue(int offset, int a, Object[] result) {
		GrowingByteArray array2 = this.array;
		offset = decodeValue(offset, a, result, array2);
		return offset;
	}

	boolean contains(int handle) {
		return this.getValueCount(handle) > 0;
	}

	public int load(GrowingByteArray from, int position) {
		this.array = new GrowingByteArray(100);
		this.table = new PositiveIntToIntHashTable(100);
		final int size = from.getInt(position);
		position += 4;
		for (int a = 0; a < size; a++) {
			final int key = from.getInt(position);
			position += 4;
			final int len = from.getInt(position);
			position += 4;
			final int lenBytes = from.getInt(position);
			position += 4;
			this.table.put(key, this.array.getSize() + 1);
			this.array.add(len);
			this.array.add(lenBytes);
			for (int b = 0; b < lenBytes; b++) {
				this.array.add(from.get(position++));
			}
		}
		return position;
	}

	public synchronized void store(GrowingByteArray barray) {
		barray.add(this.table.size);
		
		for (int a = 0; a < this.table.intTable.length; a++) {
			final int i = this.table.intTable[a];
			a++;
			if (i != 0) {
				barray.add(i);
				int address = this.table.intTable[a] - 1;
				final int len = this.array.getInt(address);
				if (len < 0) {
					final Object[] objects = this.editedEntries.get(address);
					this.writeValues(barray, objects);
				} else {
					barray.add(len);
					address += 4;
					final int lenBytes = this.array.getInt(address);
					barray.add(lenBytes);
					address += 4;
					for (int b = 0; b < lenBytes; b++) {
						barray.add(this.array.get(address++));
					}
				}
			}
		}
	}

	private final Object[] temp = new Object[1];

	public Object getValue(int id) {
		int i = this.table.get(id) - 1;
		if (i == -1) {
		 	return null;
		}
		final int length = this.array.getInt(i);
		if (length < 0) {
			final Object[] objects = this.editedEntries.get(i);
			if (objects.length == 0) {
				return null;
			}
			return objects[0];
		}
		final Object[] result = this.temp;
		i += 8;
		for (int a = 0; a < length; a++) {
			i = this.decodeValue(i, a, result);
			return this.temp[0];
		}
		return null;
	}

	public void remove(int id) {
		final int i = this.table.get(id) - 1;
		if (i == -1) {
			return;
		}
		final int int1 = this.array.getInt(i);
		if (int1 < 0) {
			this.editedEntries.remove(int1);
		}
		this.table.delete(id);
	}
}