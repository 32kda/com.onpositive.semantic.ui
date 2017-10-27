package com.onpositive.datastorage.tests;

import java.util.Arrays;

import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.MultiValueTable;

public class MVTest1 {

	public static void main(String[] args) {
		final MultiValueTable va = new MultiValueTable(null);
		va.setValues(1, "Pavel");
		va.setValues(2, "Petrochenko");
		final GrowingByteArray barray = new GrowingByteArray(1);
		va.store(barray);
		va.load(barray, 0);
		final GrowingByteArray bArray2 = new GrowingByteArray(1);
		va.store(bArray2);
		System.out.println(va.getValue(1));
		System.out.println(va.getValue(2));
		if (!Arrays.equals(barray.array(), bArray2.array())) {
			System.out.println("Error");
		}
	}
}
