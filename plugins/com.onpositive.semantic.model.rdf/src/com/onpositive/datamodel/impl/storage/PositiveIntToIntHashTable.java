package com.onpositive.datamodel.impl.storage;

import java.nio.ByteBuffer;

public final class PositiveIntToIntHashTable implements IStorableInByteBuffer {

	public int[] intTable;
	int ln;
	int size;
	int treshold;

	public void store(GrowingByteArray ba) {
		ba.add(this.size);
		ba.add(this.ln);
		for (int a = 0; a < this.intTable.length; a++) {
			final int key = this.intTable[a];
			a++;
			if (key != 0) {
				final int value = this.intTable[a];
				ba.add(key);
				ba.add(value);
			}
		}
	}

	public int load(GrowingByteArray ba, int position) {
		final int sa = ba.getInt(position);
		position += 4;
		final int cap = ba.getInt(position);
		position += 4;
		final PositiveIntToIntHashTable result = new PositiveIntToIntHashTable(cap);
		for (int a = 0; a < sa; a++) {
			final int key = ba.getInt(position);
			position += 4;
			final int value = ba.getInt(position);
			position += 4;
			result.put(key, value);
		}
		this.intTable = result.intTable;
		this.ln = result.ln;
		this.size = result.size;
		this.treshold = result.treshold;
		return position;
	}

	public static PositiveIntToIntHashTable read(ByteBuffer ba) {
		final int sa = BitUtils.read(ba);
		final int cap = BitUtils.read(ba);
		final PositiveIntToIntHashTable result = new PositiveIntToIntHashTable(cap);
		for (int a = 0; a < sa; a++) {
			final int key = BitUtils.read(ba);
			final int value = BitUtils.read(ba);
			result.put(key, value);
		}
		return result;
	}

	public int getSize() {
		return this.size;
	}

	public int getCapacity() {
		return this.ln;
	}

	public PositiveIntToIntHashTable(int capacity) {
		if (capacity < 10) {
			capacity = 10;
		}
		this.intTable = new int[capacity * 2];
		this.ln = capacity;
		this.treshold = (capacity * 3) / 4 - 1;
	}

	public int get(int key) {
		final int hash = (key + 1) % this.ln;
		int offset = hash << 1;
		int i = this.intTable[offset];
		while (i != 0) {
			if (i == key) {
				return this.intTable[offset + 1];
			}
			offset += 2;
			if (offset >= this.intTable.length) {
				offset = 0;
			}
			i = this.intTable[offset];
		}
		return 0;
	}

	public void put(int key, int value) {
		final int hash = (key + 1) % this.ln;
		int offset = hash << 1;
		int i = this.intTable[offset];
		while (i != 0) {
			if (i == key) {
				this.intTable[offset + 1] = value;
				return;
			}
			offset += 2;
			if (offset >= this.intTable.length) {
				offset = 0;
			}
			i = this.intTable[offset];
		}
		this.intTable[offset] = key;
		this.intTable[offset + 1] = value;
		this.size++;
		if (this.size > this.treshold) {
			this.rehash();
			return;
		}
	}

	public void delete(int key) {
		final int hash = (key + 1) % this.ln;
		int offset = hash << 1;
		int i = this.intTable[offset];
		while (i != 0) {
			if (i == key) {
				this.intTable[offset] = 0;
				this.intTable[offset + 1] = 0;
				final PositiveIntToIntHashTable positiveIntToIntHashTable = new PositiveIntToIntHashTable(
						this.size - 1);
				this.refill(positiveIntToIntHashTable);
				return;
			}
			offset += 2;
			if (offset >= this.intTable.length) {
				offset = 0;
			}
			i = this.intTable[offset];
		}
	}

	private void rehash() {
		final PositiveIntToIntHashTable positiveIntToIntHashTable = new PositiveIntToIntHashTable(
				this.size * 2);
		this.refill(positiveIntToIntHashTable);
	}

	private void refill(PositiveIntToIntHashTable positiveIntToIntHashTable) {
		for (int a = 0; a < this.intTable.length; a++) {
			final int kk = this.intTable[a];
			a++;
			if (kk != 0) {
				final int el = this.intTable[a];
				positiveIntToIntHashTable.put(kk, el);
			}
		}
		this.intTable = positiveIntToIntHashTable.intTable;
		this.size = positiveIntToIntHashTable.size;
		this.ln = positiveIntToIntHashTable.ln;
		this.treshold = positiveIntToIntHashTable.treshold;
	}

	public static abstract class Callback {
		public abstract boolean visit(int key, int value);
	}

	public void traverse(Callback cl) {
		for (int a = 0; a < this.intTable.length; a++) {
			final int kk = this.intTable[a];
			a++;
			if (kk != 0) {
				final int el = this.intTable[a];
				if (!cl.visit(kk, el)) {
					return;
				}
			}
		}
	}

}