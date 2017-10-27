package com.onpositive.semantic.model.java.tests;

import java.awt.Point;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.OnModify;
import com.onpositive.semantic.model.api.validation.IValidator;

public class BasicPlatformExtensionTest extends TestCase{

	
	@SuppressWarnings("rawtypes")
	public void test0(){
		IHasMeta meta = MetaAccess.getMeta(this);
		IValidator service = DefaultMetaKeys.getService(meta, IValidator.class);
		TestCase.assertNull(service);
		ITestServiceClass service2 = DefaultMetaKeys.getService(meta, ITestServiceClass.class);
		TestCase.assertEquals(service2.sayHello(), "Hello");
		meta=MetaAccess.getMeta(new Point());
		service2 = DefaultMetaKeys.getService(meta, ITestServiceClass.class);
		TestCase.assertNull(service2);
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
	
	static class Tr extends LL{
		
		@OnModify
		void onChange(){
			x++;
		}
	}
	
	static int k=0;
	
	
	public void test17(){
		LL d=new LL();
		PropertyAccess.setValue("x", d, 2);
		TestCase.assertTrue(d.x==4);
	}
	
	public void test18(){
		LL d=new LL1();
		PropertyAccess.setValue("x", d, 2);
		TestCase.assertTrue(d.x==4);
	}
	
	public void test19(){
		LL d=new Tr();
		LL d1=new Tr();
		CompositeCommand cc=new CompositeCommand();
		cc.addCommand(PropertyAccess.createSetValueCommand("x",d, 2));
		cc.addCommand(PropertyAccess.createSetValueCommand("x",d1, 2));
		cc.execute();
		TestCase.assertTrue(d.x==4);
		TestCase.assertTrue(k==2);
	}
}
