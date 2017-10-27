package com.onpositive.datastorage.tests;

import java.util.HashSet;
import java.util.Random;

import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.PositiveIntToIntHashTable;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final PositiveIntToIntHashTable ii = new PositiveIntToIntHashTable(100);
		for (int a = 1; a < 100; a++) {
			ii.put(a, a + 1);
		}

		for (int a = 1; a < 100; a++) {
			final int i = ii.get(a);
			if (i != a + 1) {
				throw new RuntimeException();
			}
		}

		for (int a = 1; a < 100; a++) {
			ii.delete(a);
		}
		if (ii.getSize() != 0) {
			throw new RuntimeException();
		}
		for (int a = 1; a < 1000; a++) {
			final int i = ii.get(a);
			if (i != 0) {
				throw new RuntimeException();
			}
		}
		System.out.println("Completed"); //$NON-NLS-1$

		final Random random = new Random();
		final HashSet<Integer> ks = new HashSet<Integer>();
		for (int a = 1; a < 1000; a++) {
			final int i = random.nextInt(10000) + 1;
			ii.put(i, i * 2);
			ks.add(i);
		}
		final GrowingByteArray ba = new GrowingByteArray(100);
		ii.store(ba);
		System.out.println(ks.size());
		System.out.println(ba.getSize());
		final PositiveIntToIntHashTable ii1 = PositiveIntToIntHashTable.read(ba
				.toByteBuffer());
		for (final Integer i : ks) {
			final int j = ii1.get(i);
			ii1.delete(i);
			if (j != i * 2) {
				throw new RuntimeException();
			}
		}
		System.out.println(ii1.getCapacity());
		System.out.println("Ok"); //$NON-NLS-1$
	}
}
