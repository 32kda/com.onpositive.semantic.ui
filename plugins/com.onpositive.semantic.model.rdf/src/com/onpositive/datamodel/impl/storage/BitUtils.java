package com.onpositive.datamodel.impl.storage;

import java.nio.ByteBuffer;

public class BitUtils {

	public static byte char1(char x) {
		return (byte) (x >> 8);
	}

	public static byte char0(char x) {
		return (byte) (x >> 0);
	}

	public static char makeChar(byte b1, byte b0) {
		return (char) ((b1 << 8) | (b0 & 0xff));
	}

	public static byte long7(long x) {
		return (byte) (x >> 56);
	}

	public static byte long6(long x) {
		return (byte) (x >> 48);
	}

	public static byte long5(long x) {
		return (byte) (x >> 40);
	}

	public static byte long4(long x) {
		return (byte) (x >> 32);
	}

	public static byte long3(long x) {
		return (byte) (x >> 24);
	}

	public static byte long2(long x) {
		return (byte) (x >> 16);
	}

	public static byte long1(long x) {
		return (byte) (x >> 8);
	}

	public static byte long0(long x) {
		return (byte) (x >> 0);
	}

	public static byte int3(int x) {
		return (byte) (x >> 24);
	}

	public static byte int2(int x) {
		return (byte) (x >> 16);
	}

	public static byte int1(int x) {
		return (byte) (x >> 8);
	}

	public static byte int0(int x) {
		return (byte) (x >> 0);
	}

	static int read(ByteBuffer ba) {
		final byte b0 = ba.get();
		final byte b1 = ba.get();
		final byte b2 = ba.get();
		final byte b3 = ba.get();
		return makeInt(b3, b2, b1, b0);
	}

	static public int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
	}

	static public long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3,
			byte b2, byte b1, byte b0) {
		return ((((long) b7 & 0xff) << 56) | (((long) b6 & 0xff) << 48)
				| (((long) b5 & 0xff) << 40) | (((long) b4 & 0xff) << 32)
				| (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16)
				| (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff) << 0));
	}
}
