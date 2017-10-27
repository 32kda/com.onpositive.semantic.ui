package com.onpositive.datastorage.tests;

import java.io.IOException;
import java.util.Arrays;

import com.onpositive.datamodel.impl.storage.FileHeapStorage;

public class FileStorageTest {

	public static void main(String[] args) {
		try {
			FileHeapStorage sm = new FileHeapStorage(null,"C:/content/storage");
			sm.delete();
			sm = new FileHeapStorage(null,"C:/content/storage");
			//ArrayList<Integer>za=new ArrayList<Integer>();
			long l0=System.currentTimeMillis();
			int i = 20000;
			for (int a = 0; a < i; a++) {
				sm.setValues("a"+a, "Hello World" + a);
				System.out.println(a);
			}
			long l1=System.currentTimeMillis();
			System.out.println(l1-l0);
			
			System.out.println(sm.size());
			for (int a = 0; a < i; a++) {
				System.out.println(Arrays.toString(sm.getValues("a"+a)));				
			}
			System.out.println(sm.size());
			for (int a = 0; a < i; a++) {
				sm.setValues("a"+a, "Hello World" + a);				
			}
			System.out.println(sm.size());
			sm.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
