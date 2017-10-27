package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;

import org.junit.Test;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;

public class FilteringSampleTest {

	static class Person{
		int age;
		String name;
		public Person(int age, String name) {
			super();
			this.age = age;
			this.name = name;
		}
		@Override
		public String toString() {
			return name+":"+age;
		}
	}
	
	@Test
	public void test() {
		ArrayList<Person>os=new ArrayList<FilteringSampleTest.Person>();
		os.add(new Person(13,"Pavel"));
		os.add(new Person(6,"Artem"));
		os.add(new Person(8,"Denis"));
		Object calculate = ExpressionAccess.calculate("this orderBy [-age]",os);
		System.out.println(calculate);
	}
}
