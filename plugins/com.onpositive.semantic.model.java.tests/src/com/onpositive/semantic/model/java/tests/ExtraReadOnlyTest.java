package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;

import junit.framework.TestCase;

public class ExtraReadOnlyTest extends TestCase {

	@ReadOnly(value="$.readOnly")
	static class A{
		
		boolean readOnly;
		
		int x;
	}
	
	public void test0(){
		A o = new A();
		TestCase.assertFalse(PropertyAccess.isReadonly(PropertyAccess.getProperty(o, "x"), o));
		o.readOnly=true;
		TestCase.assertTrue(PropertyAccess.isReadonly(PropertyAccess.getProperty(o, "x"), o));
	}
}
