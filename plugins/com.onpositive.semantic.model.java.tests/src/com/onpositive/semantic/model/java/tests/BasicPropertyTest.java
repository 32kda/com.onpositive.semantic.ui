package com.onpositive.semantic.model.java.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider.ClassPropertyInfo;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;

public class BasicPropertyTest extends TestCase {

	public void test0() {
		Point p = new Point();
		Collection<Object> pa = (Collection<Object>) ValueUtils
				.toCollection(PropertyAccess.getProperties(p));
		TestCase.assertTrue(pa.size() == 6);
	}

	public void test1() {
		Point p = new Point();
		Object calculate = ExpressionAccess.calculate("@status.error", p);
		TestCase.assertEquals(calculate, false);
	}

	public void test2() {
		Point p = new Point();
		Object calculate = ExpressionAccess.calculate("@label", p);
		TestCase.assertEquals(calculate, "java.awt.Point[x=0,y=0]");
	}

	public void test3() {
		Point p = new Point();
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"@properties", p);
		TestCase.assertTrue(calculate.size() == 6);
	}

	static class A {
		int a;
	}

	static class B extends A {
		int b;
	}

	@SuppressWarnings("rawtypes")
	public void test4() {
		B p = new B();
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"@properties", p);		
		if (calculate.size()!=2){
			System.out.println(calculate);
		}
		TestCase.assertTrue(calculate.size() == 2);
	}

	@SuppressWarnings("rawtypes")
	public void test5() {
		B p = new B();
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"@properties", p);
		TestCase.assertTrue(calculate.size() == 2);
		ExpressionValueProperty gm = new ExpressionValueProperty("a+b", null);
		ClassPropertyInfo properties = JavaPropertyProvider.instance.getProperties(B.class);
		properties.register(
				gm);
		calculate = (Collection) ExpressionAccess.calculate(
				"@properties", p);
		TestCase.assertTrue(calculate.size() == 3);
		JavaPropertyProvider.instance.getProperties(B.class).remove(gm);
	}
	// tests on property meta

	
	class PartiallyReadOnly{
		Point location;
		
		@ReadOnly("$.location==null")
		String name;
	}
	
	public void test6(){
		PartiallyReadOnly m=new PartiallyReadOnly();
		IProperty property = PropertyAccess.getProperty(m, "name");
		TestCase.assertTrue(PropertyAccess.isReadonly(property, m));
		m.location=new Point();
		TestCase.assertTrue(!PropertyAccess.isReadonly(property, m));
	}
	

	public void testPublic() {
		Point p = new Point();
		LinkedHashSet<IProperty> publicProperties = PropertyAccess.getPublicProperties(p);
		TestCase.assertTrue(publicProperties.size()==5);
	}
	public void testEditable() {
		Point p = new Point();
		LinkedHashSet<IProperty> publicProperties = PropertyAccess.getEditableProperties(p);
		TestCase.assertTrue(publicProperties.size()==3);
	}
	public void testSystem() {
		Point p = new Point();
		LinkedHashSet<IProperty> publicProperties = PropertyAccess.getSystemProperties(p);
		TestCase.assertTrue(publicProperties.size()==5);
	}
	
	public void test7() {
		Snippet003RadioAndGroups p = new Snippet003RadioAndGroups();
		TestCase.assertEquals("Left",DefaultMetaKeys.getCaption(PropertyAccess.getProperty(p, "GoLeft")));
	}
	
	
	public class Snippet003RadioAndGroups {

		boolean goLeft;
		boolean goRight;
		boolean goForward;

		@Caption("Left")
		public boolean isGoLeft() {
			return this.goLeft;
		}

		public void setGoLeft(boolean goLeft) {
			this.goLeft = goLeft;
			if (goLeft) {
				System.out.println("Left"); //$NON-NLS-1$
			}
		}

		@Caption("%Right")
		public boolean isGoRight() {
			return this.goRight;
		}

		public void setGoRight(boolean goRight) {
			this.goRight = goRight;
			if (goRight) {
				System.out.println("Right"); //$NON-NLS-1$
			}
		}

		@Caption("%Forward")
		public boolean isGoForward() {
			return this.goForward;
		}

		public void setGoForward(boolean goForward) {

			this.goForward = goForward;
			if (goForward) {
				System.out.println("Forward"); //$NON-NLS-1$
			}
		}
	}
	
	public static class SubjectType1{
		
		ArrayList<Point>pt0;
	}
	public static class SubjectType2{
		
		Point[] pt0;
	}
	public static class SubjectType3{
		
		Set<Point> pt0;
	}
	

	
	public void test8(){
		SubjectType1 subjectType1 = new SubjectType1();
		TestCase.assertTrue(DefaultMetaKeys.getSubjectClass(PropertyAccess.getProperty(subjectType1, "pt0"))==Point.class);
	}
	public void test9(){
		SubjectType2 subjectType1 = new SubjectType2();
		TestCase.assertTrue(DefaultMetaKeys.getSubjectClass(PropertyAccess.getProperty(subjectType1, "pt0"))==Point.class);
	}
	public void test10(){
		SubjectType3 subjectType1 = new SubjectType3();
		TestCase.assertTrue(DefaultMetaKeys.getSubjectClass(PropertyAccess.getProperty(subjectType1, "pt0"))==Point.class);
	}
}
