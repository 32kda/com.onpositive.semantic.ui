package com.onpositive.datastorage.tests;

import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.impl.storage.MultiValueTable;

public class EditTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final MultiValueTable vl = new MultiValueTable(null);
		vl.setValues(2, "A"); //$NON-NLS-1$
		vl.setValues(2, "B"); //$NON-NLS-1$
		final GrowingByteArray ba = new GrowingByteArray(100);
		vl.store(ba);
		final MultiValueTable vl1 = new MultiValueTable(null);
		vl1.load(ba, 0);
		System.out.println(vl.getValue(2));
		System.out.println(vl1.getValue(2));
		vl.setValues(2);
		System.out.println(vl.getValue(2));
	}

}
