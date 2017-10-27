package com.onpositive.semantic.model.java.tests;

import junit.framework.TestCase;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.realm.FixedTypeRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;

public class FixedTypeTest extends TestCase {

	public static class Person {
		String name;
		int age;

		public Person(String name, int age) {
			super();
			this.name = name;
			this.age = age;
		}
	}

	int c=0;
	public void test0() {
		FixedTypeRealm<Person> ps = new FixedTypeRealm<FixedTypeTest.Person>(
				Person.class){
			
			/**
			* 
			*/
			private static final long serialVersionUID = 1L;

			@Override
			public void changed(Iterable<Person> element) {
				super.changed(element);
				c++;
			}
			
			
		};
		c=0;
		Person object = new Person("a", 1);
		RealmAccess.addElement(ps, object);
		PropertyAccess.setValue("age", object,7);
		TestCase.assertTrue(ps.contains(object));
		TestCase.assertTrue(c==1);		
	}
}