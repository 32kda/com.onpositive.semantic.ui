package com.onpositive.datastorage.tests;

import java.util.Arrays;

import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.SimpleStructuredStorage;

public class PropertyTableTest {

	private static final String ID = "id"; //$NON-NLS-1$
	private static final int LIMIT = 800000;

	public static void main(String[] args) {
		final SimpleStructuredStorage propertyTable = new SimpleStructuredStorage(
				null);
		for (int a = 1; a < LIMIT; a++) {
			propertyTable.setValues(a, ID, new Object[] { (long) 1 });
		}
		Object[] values = propertyTable.getValues(1, ID);
		System.out.println(Arrays.toString(values));
		final GrowingByteArray bs = new GrowingByteArray(100);
		propertyTable.store(bs);
		final SimpleStructuredStorage multiValueTable1 = new SimpleStructuredStorage(
				null);
		multiValueTable1.load(bs, 0);
		final long l = System.currentTimeMillis();
		for (int a = 1; a < LIMIT; a++) {
			values = multiValueTable1.getValues(a, ID);
		}
		final long l1 = System.currentTimeMillis();
		System.out.println(bs.getSize());
		System.out.println(Arrays.toString(values));
		System.out.println("TIming:" + (l1 - l)); //$NON-NLS-1$
	}
}
