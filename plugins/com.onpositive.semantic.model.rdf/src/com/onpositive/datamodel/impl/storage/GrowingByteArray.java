package com.onpositive.datamodel.impl.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class GrowingByteArray implements IStorableInByteBuffer {

	byte[] bytes;
	int size;

	public GrowingByteArray(byte[]array){
		this.bytes=array;
		this.size=array.length;
	}
	
	public GrowingByteArray(int cap) {
		this.bytes = new byte[cap];
	}

	public void add(byte bt) {
		this.bytes[this.size] = bt;
		this.size++;
		if (this.size == this.bytes.length) {
			this.resize();
		}
	}

	public void add(IStorableInByteBuffer bf) {
		bf.store(this);
	}

	public int getInt(int position) {
		final byte b0 = this.bytes[position];
		final byte b1 = this.bytes[position + 1];
		final byte b2 = this.bytes[position + 2];
		final byte b3 = this.bytes[position + 3];
		return BitUtils.makeInt(b3, b2, b1, b0);
	}

	public ByteBuffer toByteBuffer() {
		return ByteBuffer.wrap(this.bytes, 0, this.size);
	}

	public void add(int vl) {
		this.add(BitUtils.int0(vl));
		this.add(BitUtils.int1(vl));
		this.add(BitUtils.int2(vl));
		this.add(BitUtils.int3(vl));
	}

	private void resize() {
		final byte[] bs = new byte[(this.bytes.length * 3) / 2 + 2];
		System.arraycopy(this.bytes, 0, bs, 0, this.bytes.length);
		this.bytes = bs;
	}

	public byte get(int position) {
		return this.bytes[position];
	}

	public int getSize() {
		return this.size;
	}

	public int getCapacity() {
		return this.bytes.length;
	}

	public void add(long longValue) {
		final byte long0 = BitUtils.long0(longValue);
		this.add(long0);
		final byte long1 = BitUtils.long1(longValue);
		this.add(long1);
		final byte long2 = BitUtils.long2(longValue);
		this.add(long2);
		final byte long3 = BitUtils.long3(longValue);
		this.add(long3);
		final byte long4 = BitUtils.long4(longValue);
		this.add(long4);
		final byte long5 = BitUtils.long5(longValue);
		this.add(long5);
		final byte long6 = BitUtils.long6(longValue);
		this.add(long6);
		final byte long7 = BitUtils.long7(longValue);
		this.add(long7);
	}

	public void set(int position, byte vl) {
		this.bytes[position] = vl;
	}

	public void set(int position, int vl) {
		this.set(position, BitUtils.int0(vl));
		this.set(position + 1, BitUtils.int1(vl));
		this.set(position + 2, BitUtils.int2(vl));
		this.set(position + 3, BitUtils.int3(vl));
	}

	public void store(GrowingByteArray ba) {
		ba.add(this.size);
		for (int a = 0; a < this.size; a++) {
			ba.bytes[ba.size] = this.bytes[a];
			ba.size++;
			if (ba.size == ba.bytes.length) {
				final byte[] bs = new byte[(ba.bytes.length * 3) / 2 + 2];
				System.arraycopy(ba.bytes, 0, bs, 0, ba.bytes.length);
				ba.bytes = bs;
			}
		}
	}

	public int load(GrowingByteArray array, int position) {
		final int size = array.getInt(position);
		position += 4;

		position += size;
		return position;
	}

	public void add(char c) {
		if (c < 126) {
			this.add((byte) c);
			return;
		} else {
			this.add((byte) 127);
			this.add(BitUtils.char0(c));
			this.add(BitUtils.char1(c));
		}
	}

	public char getChar(int offset) {
		final byte b = this.bytes[offset];
		if (b < 126) {
			return (char) b;
		} else {
			final byte b0 = this.bytes[offset + 1];
			final byte b1 = this.bytes[offset + 2];
			return BitUtils.makeChar(b0, b1);
		}
	}

	public void add(String intv) {
		final int length = intv.length();
		for (int a = 0; a < length; a++) {
			final char charAt = intv.charAt(a);
			this.add(charAt);
		}
		this.add((byte) 0);
	}

	public int readString(int offset, StringBuilder bld) {
		while (true) {
			final byte b = this.bytes[offset];
			offset++;
			if (b == 0) {
				return offset;
			}
			if (b < 126) {
				bld.append((char) b);
			} else {
				final byte b0 = this.bytes[offset++];
				final byte b1 = this.bytes[offset++];
				bld.append(BitUtils.makeChar(b1, b0));
			}
		}
	}

	public int skipString(int offset) {
		while (true) {
			final byte b = this.bytes[offset];
			offset++;
			if (b == 0) {
				return offset;
			}
			if (b < 126) {

			} else {
				offset += 2;
			}
		}
	}

	public void write(OutputStream fs) throws IOException {
		fs.write(this.bytes, 0, this.getSize());
	}

	public void load(InputStream fileInputStream) throws IOException {
		int left = this.bytes.length;
		int pos = 0;
		while (true) {
			final int red = fileInputStream.read(this.bytes, pos, left);
			if (red == -1) {
				this.size = left;
				return;
			}
			if (red == 0) {
				break;
			}
			left -= red;
			pos += red;

			if (left == 0) {
				this.resize();
			}
		}
	}

	public byte[] array() {
		return this.bytes;
	}
}
