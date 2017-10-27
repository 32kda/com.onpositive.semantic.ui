package com.onpositive.semantic.model.java.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public class BasicExpressionsTest extends TestCase {

	public void test0() {
		Object calculate = ExpressionAccess.calculate("this+5", 5);
		TestCase.assertEquals(calculate, 10);
	}

	public void test1() {
		Object calculate = ExpressionAccess.calculate("Hello {this+5}", 5);
		TestCase.assertEquals(calculate, "Hello 10");
	}

	public void test2() {
		Object calculate = ExpressionAccess.calculate("{this+5}", 5);
		TestCase.assertEquals(calculate, 10);
	}
	
	public void test19() {
		Object calculate = ExpressionAccess.calculate("this+5+5", 5);
		TestCase.assertEquals(calculate, 15);
	}

	public void test30() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Object calculate = ExpressionAccess.calculate("$ - this", 15, 5);
		TestCase.assertEquals(calculate, 10);
	}

	public void test32() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Object calculate = ExpressionAccess.calculate("$ and this", true, true);
		TestCase.assertEquals(calculate, true);
		calculate = ExpressionAccess.calculate("$ and this", true, false);
		TestCase.assertEquals(calculate, false);
	}

	public void test33() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Object calculate = ExpressionAccess.calculate("$ or this", true, true);
		TestCase.assertEquals(calculate, true);
		calculate = ExpressionAccess.calculate("$ or this", true, false);
		TestCase.assertEquals(calculate, true);
		calculate = ExpressionAccess.calculate("$ or this", false, false);
		TestCase.assertEquals(calculate, false);
	}

	@SuppressWarnings("rawtypes")
	public void test35() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		ArrayList<Integer> parent = new ArrayList<Integer>();
		parent.add(10);
		parent.add(4);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"$ filterBy [this<5]", parent, true);
		TestCase.assertTrue(calculate.size() == 1);
		TestCase.assertTrue(calculate.contains(4));
	}

	@SuppressWarnings("rawtypes")
	public void test36() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		ArrayList<Integer> parent = new ArrayList<Integer>();
		parent.add(10);
		parent.add(4);
		parent.add(2);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"($ filterBy [this<5]) filterBy [this<3]", parent, true);
		TestCase.assertTrue(calculate.size() == 1);
		TestCase.assertTrue(calculate.contains(2));
	}

	@SuppressWarnings("rawtypes")
	public void test37() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"'Hello',4", null, true);
		TestCase.assertTrue(calculate.size() == 2);
		TestCase.assertTrue(calculate.contains(4));
		TestCase.assertTrue(calculate.contains("Hello"));
	}

	@SuppressWarnings("rawtypes")
	public void test38() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		ArrayList<Integer> parent = new ArrayList<Integer>();
		parent.add(10);
		parent.add(4);
		parent.add(2);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"$ filterBy [this<5] filterBy [this<3]", parent, true);
		TestCase.assertTrue(calculate.size() == 1);
		TestCase.assertTrue(calculate.contains(2));
	}

	@SuppressWarnings("rawtypes")
	public void test39() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"(1,4,2) orderBy [this]", null, true);
		ArrayList r = (ArrayList) calculate;
		TestCase.assertTrue(r.size() == 3);
		TestCase.assertTrue(r.get(0).equals(1));
		TestCase.assertTrue(r.get(1).equals(2));
		TestCase.assertTrue(r.get(2).equals(4));
	}

	@SuppressWarnings("rawtypes")
	public void test391() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"(1,4,2) orderBy [-this]", null, true);
		ArrayList r = (ArrayList) calculate;
		TestCase.assertTrue(r.size() == 3);
		TestCase.assertTrue(r.get(0).equals(4));
		TestCase.assertTrue(r.get(1).equals(2));
		TestCase.assertTrue(r.get(2).equals(1));
	}

	@SuppressWarnings("rawtypes")
	public void test392() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Collection calculate = (Collection) ExpressionAccess.calculate(
				"(1,4,2) transformBy [-this]", null, true);
		ArrayList r = (ArrayList) calculate;
		TestCase.assertTrue(r.size() == 3);
		TestCase.assertTrue(r.get(0).equals(-1));
		TestCase.assertTrue(r.get(1).equals(-4));
		TestCase.assertTrue(r.get(2).equals(-2));
	}

	public void test31() {
		// ArrayList<Object> ls = new ArrayList<Object>();
		// ls.add(5);
		Object calculate = ExpressionAccess.calculate("$ - this-4", 15, 5);
		TestCase.assertEquals(calculate, 6);
	}

	public void test3() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess.calculate("$ contains this", ls, 5);
		TestCase.assertEquals(calculate, true);
	}

	public void test4() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess.calculate(
				"this instanceof class:java.lang.String", 5);
		TestCase.assertEquals(false, calculate);
	}

	public void test44() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess.calculate(
				"this instanceof class:java.util.Collection", ls);
		TestCase.assertEquals(calculate, true);
	}

	public void test42() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess.calculate(
				"this instanceof class:java.lang.String", "5");
		TestCase.assertEquals(true, calculate);
	}

	public void test40() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess.calculate(
				"this instanceof java.lang.String", 5);
		TestCase.assertEquals(false, calculate);
	}

	public void test41() {

		Object calculate = ExpressionAccess.calculate(
				"this instanceof java.lang.String", "5");
		TestCase.assertEquals(true, calculate);
	}

	public void test5() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess
				.calculate(
						"new com.onpositive.semantic.model.java.tests.BasicExpressionsTest",
						MetaAccess.getMeta(BasicExpressionsTest.class), null,
						null);
		TestCase.assertEquals(calculate instanceof BasicExpressionsTest, true);
	}

	public void test6() {
		ArrayList<Object> ls = new ArrayList<Object>();
		ls.add(5);
		Object calculate = ExpressionAccess
				.calculate(
						"new class:com.onpositive.semantic.model.java.tests.BasicExpressionsTest",
						MetaAccess.getMeta(BasicExpressionsTest.class), null,
						null);
		TestCase.assertEquals(calculate instanceof BasicExpressionsTest, true);
	}

	public void test7() {
		ExpressionValueProperty t = new ExpressionValueProperty("this+5", null);
		TestCase.assertEquals(t.getValue(5), 10);
	}

	public void test8() {
		Point p = new Point();
		p.x = 5;
		ExpressionValueProperty t = new ExpressionValueProperty("this.x+5",
				null);
		TestCase.assertEquals(t.getValue(p), 10);
	}

	static int pa;

	public void test10() {
		pa = 0;
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"(this.x*this.x+this.y*this.y)", null);
		Point p = new Point();
		p.x = 2;
		p.y = 2;
		JavaPropertyProvider.instance.getProperties(Point.class).register(gm);
		ExpressionValueProperty t = new ExpressionValueProperty("this.range>8",
				null);
		TestCase.assertFalse((Boolean) t.getValue(p));
		p.y = 3;
		TestCase.assertTrue((Boolean) t.getValue(p));
		PropertyAccess.addPropertyStructureListener(t,
				new IValueListener<Object>() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChanged(Object oldValue, Object newValue) {
						pa++;
					}
				});
		gm.setExpressionString("x*x+y*y-10");
		TestCase.assertTrue(pa == 1);
		TestCase.assertFalse((Boolean) t.getValue(p));
	}

	public void test11() {
		pa = 0;
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"(this.x*this.x+this.y*this.y)", null);
		Point p = new Point();
		p.x = 2;
		p.y = 2;
		JavaPropertyProvider.instance.getProperties(Point.class).register(gm);
		ExpressionValueProperty t = new ExpressionValueProperty(
				"this.range>8&&this.range>7", null);
		TestCase.assertFalse((Boolean) t.getValue(p));
		p.y = 3;
		TestCase.assertTrue((Boolean) t.getValue(p));
		PropertyAccess.addPropertyStructureListener(t,
				new IValueListener<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChanged(Object oldValue, Object newValue) {
						pa++;
					}
				});
		gm.setExpressionString("x*x+y*y-10");
		TestCase.assertTrue(pa == 1);
		TestCase.assertFalse((Boolean) t.getValue(p));
	}

	public void test12() {
		pa = 0;
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"(this.x+$.y)", null);
		VariableExpression parentContext = new VariableExpression();
		Point value = new Point();
		value.y = 5;
		parentContext.setValue(value);
		gm.setParentContext(parentContext);
		Point p = value;
		p.x = 2;
		JavaPropertyProvider.instance.getProperties(Point.class).register(gm);
		ExpressionValueProperty t = new ExpressionValueProperty("range>=8",
				null);
		TestCase.assertFalse((Boolean) t.getValue(p));
		p.x = 3;
		TestCase.assertTrue((Boolean) t.getValue(p));
		PropertyAccess.addPropertyStructureListener(t,
				new IValueListener<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChanged(Object oldValue, Object newValue) {
						pa++;
					}
				});
		value.y = 4;
		parentContext.setValue(value);
		TestCase.assertTrue(pa == 1);
		TestCase.assertFalse((Boolean) t.getValue(p));
	}

	public void test13() {
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this->'x'", null);
		Point pt = new Point();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, 3);
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(gm));
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.x, 4);
		CodeAndMessage validate = ValidationAccess
				.validate(new DefaultValidationContext(null, pt, gm));
		TestCase.assertTrue(validate.isError());
		validate = ValidationAccess.validate(new DefaultValidationContext(1,
				pt, gm));
		TestCase.assertTrue(!validate.isError());
	}

	public void test14() {
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this->'z'", null);
		Point pt = new Point();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, null);
		TestCase.assertTrue(DefaultMetaKeys.isReadonly(gm));
		// PropertyAccess.setValue(gm, pt, 4);
		// TestCase.assertEquals(pt.x, 4);
		CodeAndMessage validate = ValidationAccess
				.validate(new DefaultValidationContext(2, pt, gm));
		TestCase.assertTrue(!validate.isError());
		try {
			PropertyAccess.setValue(gm, pt, 4);
		} catch (Exception e) {
			return;
		}
		TestCase.assertTrue(false);
	}

	public void test15() {
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this.x", null);
		Point pt = new Point();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, 3);
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(gm));
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.x, 4);
		CodeAndMessage validate = ValidationAccess
				.validate(new DefaultValidationContext(null, pt, gm));
		TestCase.assertTrue(validate.isError());
		validate = ValidationAccess.validate(new DefaultValidationContext(1,
				pt, gm));
		TestCase.assertTrue(!validate.isError());
	}

	static class CaseClass {
		boolean decision;
		int x;
		int y;
	}

	public void test16() {
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this->(decision?'y':'x')", null);
		CaseClass pt = new CaseClass();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, 3);
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(gm));
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.x, 4);
		pt.decision = true;
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.y, 4);
		CodeAndMessage validate = ValidationAccess
				.validate(new DefaultValidationContext(null, pt, gm));
		TestCase.assertTrue(validate.isError());
		validate = ValidationAccess.validate(new DefaultValidationContext(1,
				pt, gm));
		TestCase.assertTrue(!validate.isError());
	}

	public void test17() {
		ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this->(decision?'z':'x')", null);
		CaseClass pt = new CaseClass();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, 3);
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(gm));
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.x, 4);
		pt.decision = true;
		try {
			PropertyAccess.setValue(gm, pt, 4);
		} catch (IllegalStateException e) {
			TestCase.assertTrue(PropertyAccess.isReadonly(gm, pt));
			return;
		}
		TestCase.assertTrue(false);
	}

	static class Summator {
		int x;
		int y;
		
		public int sum(int x, int y) {
			return x + y;
		}

		public double range(int z) {
			return Math.sqrt(x * x + y * y + z * z);
		}

		public double range1(int z, int y) {
			return Math.sqrt(x * x + y * y + z * z);
		}
		
		public int sum(int x, int y, int z, int t) {
			return x + y + z + t;
		}
		
		public double nine() {
			return 9;
		}
		
		public static int staticMethod(int x, int y) {
			return x*y;
		}
	}

	public void test20() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 3;
		Object calculate = ExpressionAccess.calculate("this.range(4)", summator);
		TestCase.assertEquals(calculate, Math.sqrt(9 + 9 + 16));
	}

	public void test21() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 2;
		Object calculate = ExpressionAccess
				.calculate("this.range1(4,3)", summator);
		TestCase.assertEquals(calculate, Math.sqrt(9 + 9 + 16));
	}

	public void test22() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 2;
		final VariableExpression v = new VariableExpression();
		
		final ConstantExpression vv = new ConstantExpression(summator);
		@SuppressWarnings("unchecked")
		IListenableExpression<Object> calculate = ((IListenableExpression<Object>) ExpressionAccess
				.parse("this.range($)", new IExpressionEnvironment() {

					@Override
					public IClassResolver getClassResolver() {
						return null;
					}

					@Override
					public IListenableExpression<?> getBinding(String path) {
						if (path.equals("this")) {
							return vv;
						}
						return v;
					}
				}));
		v.setValue(2);
		TestCase.assertEquals(calculate.getValue(), Math.sqrt(9 + 4 + 4));
		v.setValue(4);
		TestCase.assertEquals(calculate.getValue(), Math.sqrt(9 + 4 + 16));
	}
	
	public void test23() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 2;
		Object calculate = ExpressionAccess
				.calculate("this.nine()", summator);
		TestCase.assertEquals(calculate, 9.0);
	}
	
	public void test24() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 2;
		Object calculate = ExpressionAccess
				.calculate("this.nine()", summator);
		TestCase.assertEquals(calculate, 9.0);
	}
	
	public void test25() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 3;
		Object calculate = ExpressionAccess.calculate("this.range(this.sum(2,2))", summator);
		TestCase.assertEquals(calculate, Math.sqrt(9 + 9 + 16));
	}
	
	public void test26() {
		Summator summator = new Summator();
		summator.x = 3;
		summator.y = 3;
		Object calculate = ExpressionAccess.calculate("this.sum(1,2,3,4)", summator);
		TestCase.assertEquals(10, calculate);
	}
	
	public void test27() {
		Object calculate = ExpressionAccess.calculate("this.staticMethod(2,5)", Summator.class);
		TestCase.assertEquals(10, calculate);
	}
	
	public void test28() {
		Object calculate = ExpressionAccess.calculate("class:com.onpositive.semantic.model.java.tests.SampleClass.staticMethod(2,5)", this);
		TestCase.assertEquals(10, calculate);
	}

	public void test18() {
		pa = 0;
		final ExpressionValueProperty gm = new ExpressionValueProperty("range",
				"this->($?'z':'x')", null);
		VariableExpression parentContext = new VariableExpression();
		parentContext.setValue(false);
		gm.setParentContext(parentContext);
		final CaseClass pt = new CaseClass();
		pt.x = 3;
		Object value = gm.getValue(pt);
		TestCase.assertEquals(value, 3);
		TestCase.assertTrue(!PropertyAccess.isReadonly(gm, pt));
		PropertyAccess.setValue(gm, pt, 4);
		TestCase.assertEquals(pt.x, 4);
		TestCase.assertTrue(!PropertyAccess.isReadonly(gm, pt));
		PropertyAccess.addPropertyStructureListener(gm,
				new IValueListener<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChanged(Object oldValue, Object newValue) {
						TestCase.assertTrue(PropertyAccess.isReadonly(gm, pt));
						pa++;
					}
				});
		parentContext.setValue(true);
		TestCase.assertTrue(pa == 1);
		try {
			PropertyAccess.setValue(gm, pt, 4);
		} catch (IllegalStateException e) {
			TestCase.assertTrue(PropertyAccess.isReadonly(gm, pt));
			return;
		}
		TestCase.assertTrue(false);
	}

	public void test9() {
		ExpressionValueProperty t = new ExpressionValueProperty(
				"(this.x*this.x+this.y*this.y)", null);
		Point p = new Point();
		long l0 = System.currentTimeMillis();
		int LIMIT = 10000;
		for (int a = 0; a < LIMIT; a++) {
			p.x = a;
			p.y = a + 1;
			TestCase.assertEquals(t.getValue(p), p.x * p.x + p.y * p.y);
		}
		long l1 = System.currentTimeMillis();
		System.out.println("Variable:" + (l1 - l0));
		l0 = System.currentTimeMillis();
		for (int a = 0; a < LIMIT; a++) {
			p.x = a;
			p.y = a + 1;
			TestCase.assertEquals(ExpressionAccess.calculate(
					"(this.x*this.x+this.y*this.y)", p), p.x * p.x + p.y * p.y);
		}
		l1 = System.currentTimeMillis();
		System.out.println("Calculate:" + (l1 - l0));
	}

	public void test90() {
		ExpressionValueProperty t = new ExpressionValueProperty("x+5", null);
		Point p = new Point();
		long l0 = System.currentTimeMillis();
		int LIMIT = 10000;
		for (int a = 0; a < LIMIT; a++) {
			p.x = a;
			p.y = a + 1;
			TestCase.assertEquals(t.getValue(p), p.x + 5);
		}
		long l1 = System.currentTimeMillis();
		System.out.println("Variable:" + (l1 - l0));
		l0 = System.currentTimeMillis();
		for (int a = 0; a < LIMIT; a++) {
			p.x = a;
			p.y = a + 1;
			TestCase.assertEquals(ExpressionAccess.calculate("x+5", p), p.x + 5);
		}
		l1 = System.currentTimeMillis();
		System.out.println("Calculate:" + (l1 - l0));
	}
}
