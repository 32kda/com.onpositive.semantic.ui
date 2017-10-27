package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.FixedBound;
import com.onpositive.semantic.model.api.property.java.annotations.OnModify;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.ValidationAccess;
import com.onpositive.semantic.model.java.tests.BasicAnnotationMetaTest.FixedBoundTest3;
import com.onpositive.semantic.model.java.tests.BasicAnnotationMetaTest.SimpleRealm;
import com.onpositive.semantic.model.java.tests.BasicTextLabelTest.ML;
import com.onpositive.semantic.model.java.tests.BasicTextLabelTest.ML1;

public class BasicCommandTests extends TestCase{

	public void test1(){
		ML target = new ML();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target, 1);
		CodeAndMessage validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(!validate.isError());
	}
	public void test2(){
		ML1 target = new ML1();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target, 1);
		CodeAndMessage validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(validate.isError());
	}
	@SuppressWarnings("rawtypes")
	public void test3(){
		ML1 target = new ML1();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target,new ArrayList());
		CodeAndMessage validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(!validate.isError());
	}
	public void test4(){
		ML target = new ML();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target,new ArrayList<Object>());
		CodeAndMessage validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(!validate.isError());
	}
	
	public void test5(){
		ML target = new ML();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target, 1);
		CompositeCommand m=new CompositeCommand();
		m.addCommand(createSetValueCommand);
		CodeAndMessage validate = ValidationAccess.validate(m);
		TestCase.assertTrue(!validate.isError());
	}
	public void test6(){
		ML1 target = new ML1();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("ms", target, 1);
		CompositeCommand m=new CompositeCommand();
		m.addCommand(createSetValueCommand);
		CodeAndMessage validate = ValidationAccess.validate(m);
		TestCase.assertTrue(validate.isError());
	}
	
	static class IntTest{
		int a;
	}
	static class IntTest1{
		Integer a;
	}
	
	public void test7(){
		IntTest target = new IntTest();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("a", target,(Object) null);
		CompositeCommand m=new CompositeCommand();
		m.addCommand(createSetValueCommand);
		CodeAndMessage validate = ValidationAccess.validate(m);
		TestCase.assertTrue(validate.isError());
	}
	public void test8(){
		IntTest1 target = new IntTest1();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("a", target,(Object) null);
		CompositeCommand m=new CompositeCommand();
		m.addCommand(createSetValueCommand);
		CodeAndMessage validate = ValidationAccess.validate(m);
		TestCase.assertTrue(!validate.isError());
	}
	public void test9(){
		FixedBoundTest3 target=new FixedBoundTest3();
		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("a", target,"v");
		CodeAndMessage validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(validate.isError());
		createSetValueCommand = PropertyAccess.createSetValueCommand("a", target,"a");
		validate = ValidationAccess.validate(createSetValueCommand);
		TestCase.assertTrue(!validate.isError());
	}
	
	public void test10(){
		FixedBoundTest3 target=new FixedBoundTest3();
		PropertyAccess.setValue("a",target, "a");		
		TestCase.assertTrue(target.a.contains("a"));
	}
	public void test11(){
		FixedBoundTest3 target=new FixedBoundTest3();
		PropertyAccess.setValue("a",target, Arrays.asList(new String[]{"a","b"}));		
		TestCase.assertTrue(target.a.contains("a"));
		TestCase.assertTrue(target.a.contains("b"));
	}
	public static class FixedBoundTest4{
		
		@FixedBound
		@RealmProvider(SimpleRealm.class)
		ArrayList<String> a=new ArrayList<String>();
	}
	
	public void test12(){
		FixedBoundTest4 target=new FixedBoundTest4();
		PropertyAccess.setValue("a",target, new String[]{"a","b"});		
		TestCase.assertTrue(target.a.contains("a"));
		TestCase.assertTrue(target.a.contains("b"));
		PropertyAccess.setValue("a",target, null);
		TestCase.assertTrue(target.a.isEmpty());		
		//Did not allow execution of invalid commands 
	}
	
	public void test13(){
		FixedBoundTest3 target=new FixedBoundTest3();
		PropertyAccess.setValue("a",target, new String[]{"a","b"});		
		TestCase.assertTrue(target.a.contains("a"));
		TestCase.assertTrue(target.a.contains("b"));
		try{
		PropertyAccess.setValue("a",target, null);
		}catch (IllegalArgumentException e) {
			return;
		}
		TestCase.assertTrue(false);
		//TestCase.assertTrue(target.a.isEmpty());		
		//Did not allow execution of invalid commands 
	}
	
	static class IT{
		int a;
	}
	
	static class IT1{
		@ReadOnly
		int a;
	}
	
	public void test14(){
		IT target=new IT();
		try{
		PropertyAccess.setValue("a",target, null);
		}catch (IllegalArgumentException e) {
			PropertyAccess.setValue("a",target, 1);
			TestCase.assertEquals(target.a, 1);
			return;
		}
		TestCase.assertTrue(false);
		//TestCase.assertTrue(target.a.isEmpty());		
		//Did not allow execution of invalid commands 
	}
	public void test15(){
		IT1 target=new IT1();
		try{
		PropertyAccess.setValue("a",target,1);
		}catch (IllegalArgumentException e) {
			return;
		}
		TestCase.assertTrue(false);
		//TestCase.assertTrue(target.a.isEmpty());		
		//Did not allow execution of invalid commands 
	}
	static class IT2{
		
		final int a=2;
	}
	public void test16(){
		IT2 target=new IT2();
		try{
		PropertyAccess.setValue("a",target,1);
		}catch (IllegalArgumentException e) {
			return;
		}
		TestCase.assertTrue(false);
		//TestCase.assertTrue(target.a.isEmpty());		
		//Did not allow execution of invalid commands 
	}
	
	static class LL{
		int x=0;
	
		@OnModify
		void onChange(){
			x++;
		}
	}

	static class LL1 extends LL{
		
		@OnModify
		void onChange(){
			x++;
		}
	}
	
	
	public void test17(){
		LL d=new LL();
		PropertyAccess.setValue("x", d, 2);
		TestCase.assertTrue(d.x==3);
	}
	
	public void test18(){
		LL d=new LL1();
		PropertyAccess.setValue("x", d, 2);
		TestCase.assertTrue(d.x==3);
	}
}
