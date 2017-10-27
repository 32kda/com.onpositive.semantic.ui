package com.onpositive.semantic.model.java.tests;

import java.awt.Point;
import java.util.ArrayList;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.DoNotUseSetter;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.java.tests.BasicRealmTest.Business;
import com.onpositive.semantic.model.java.tests.BasicRealmTest.Job;

import junit.framework.TestCase;

public class BasicBindingTest extends TestCase {

	public void test0() {
		Binding a = new Binding("");
		a.setName("hello");
		String name = DefaultMetaKeys.getCaption(a);
		TestCase.assertEquals("hello", name);
	}

	public void test1() {
		Binding a = new Binding("");
		a.setDescription("hello");
		String name = DefaultMetaKeys.getDescription(a);
		TestCase.assertEquals("hello", name);
	}
	

	public void test2() {
		Binding a = new Binding("");
		a.setReadOnly(true);
		TestCase.assertTrue(DefaultMetaKeys.isReadonly(a));
		a.setReadOnly(false);
		TestCase.assertFalse(DefaultMetaKeys.isReadonly(a));
	}

	public void test3() {
		Point object = new Point();
		Binding z = new Binding(object);
		Binding binding = z.binding("x");
		TestCase.assertTrue(DefaultMetaKeys.isRequired(binding));
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(binding));
		TestCase.assertTrue(!DefaultMetaKeys.isMultivalue(binding));
		Class<?> subjectClass = binding.getSubjectClass();
		TestCase.assertEquals(subjectClass, Integer.class);
		binding.setValue(4);
		TestCase.assertEquals(object.x, 4);
	}

	public void test4() {
		Point object = new Point();
		Binding z = new Binding(object);
		z.setAutoCommit(false);
		Binding binding = z.binding("x");
		binding.setValue(5);
		TestCase.assertEquals(object.x, 0);
		z.commit();
		TestCase.assertEquals(object.x, 5);
		object.x = 7;
		z.refresh(true);
		TestCase.assertEquals(binding.getValue(), 7);
	}

	public void test5() {
		Point object = new Point();
		Binding z = new Binding(object);
		z.setRegisterListeners(true);
		z.setAutoCommit(false);
		Binding binding = z.binding("x");
		binding.setValue(5);
		TestCase.assertEquals(object.x, 0);
		z.commit();
		TestCase.assertEquals(object.x, 5);
		object.x = 7;
		ObjectChangeManager.markChanged(object);
		TestCase.assertEquals(binding.getValue(), 7);
		z.dispose();
		object.x = 12;
		ObjectChangeManager.markChanged(object);
		TestCase.assertEquals(binding.getValue(), 7);
	}

	public void test6() {
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business = business;
		Binding bnd = new Binding(job);
		bnd.setRegisterListeners(true);
		Object calculate = ExpressionAccess.calculate("type.@realm", bnd);
		TestCase.assertTrue(calculate instanceof IRealm);
		IRealm<?> r = (IRealm<?>) calculate;
		TestCase.assertEquals(2, r.size());
		TestCase.assertEquals(business.jobTypes, new ArrayList(r.getContents()));
	}

	public void test7() {
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business = business;
		Binding bnd = new Binding(job);
		bnd.setRegisterListeners(true);
		IRealm<?> r = (IRealm<?>) bnd.binding("type").getRealm();
		TestCase.assertEquals(2, r.size());
		TestCase.assertTrue(r.contains("Pirate"));
		TestCase.assertTrue(r.contains("Salor"));
	}

	public void test8() {
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business = business;
		business.jobs.add(job);
		Binding bnd = new Binding(job);
		bnd.setRegisterListeners(true);
		IRealm<?> r = (IRealm<?>) bnd.getRealm();
		TestCase.assertEquals(1, r.size());
		TestCase.assertTrue(r.contains(job));
	}

	public void test9() {
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business = business;
		Binding bnd = new Binding(job);
		bnd.setRegisterListeners(true);
		IRealm<?> r = (IRealm<?>) bnd.binding("type").getRealm();
		TestCase.assertEquals(2, r.size());
		RealmAccess.addElement(r, "Programmer");
		TestCase.assertTrue(business.jobTypes.contains("Programmer"));
		TestCase.assertTrue(business.jobTypes.size() == 3);
	}

	public static class A {
		@ReadOnly("!$.b")
		int a;
		boolean b;
	}

	static int cnt;

	public void test11() {
		A object = new A();
		object.b = true;
		cnt = 0;
		Binding binding = new Binding(object);
		Binding binding2 = binding.binding("a");
		binding2.addBindingChangeListener(new IBindingChangeListener<Object>() {

			@Override
			public void valueChanged(ISetDelta<Object> valueElements) {
				
			}

			@Override
			public void enablementChanged(boolean isEnabled) {
				cnt++;
				TestCase.assertFalse(isEnabled);
			}

			@Override
			public void changed() {
				
			}
		});
		TestCase.assertTrue(!binding2.isReadOnly());
		object.b=false;
		ObjectChangeManager.markChanged(object);
		TestCase.assertTrue(binding2.isReadOnly());
		TestCase.assertEquals(cnt, 1);
	}
	public void test10() {
		A object = new A();
		cnt = 0;
		Binding binding = new Binding(object);
		Binding binding2 = binding.binding("a");
		TestCase.assertTrue(binding2.isReadOnly());
		object.b = true;
		binding2.addBindingChangeListener(new IBindingChangeListener<Object>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChanged(ISetDelta<Object> valueElements) {
				TestCase.assertTrue(false);
			}

			@Override
			public void enablementChanged(boolean isEnabled) {
				cnt++;
			}

			@Override
			public void changed() {
				TestCase.assertTrue(false);
			}
		});
		ObjectChangeManager.markChanged(object);
		TestCase.assertTrue(!binding2.isReadOnly());
		TestCase.assertEquals(cnt, 1);
	}
	
	public static class X{
		
		
		ArrayList<String>sm=new ArrayList<String>();

		public ArrayList<String> getSm() {
			return sm;
		}
		int v=0;

		public void setSm(ArrayList<String> sm) {
			v++;
			this.sm = sm;
		}
	}
	
public static class X1{
		
		
		ArrayList<String>sm=new ArrayList<String>();

		public ArrayList<String> getSm() {
			return sm;
		}
		int v=0;

		@DoNotUseSetter
		public void setSm(ArrayList<String> sm) {
			v++;
			this.sm = sm;
		}
	}
	
	
	public void test12() {
		X object = new X();
		Binding a = new Binding(object);
		IBinding z= (IBinding) a.getBinding("Sm");
		z.setValue("AA",null);
		TestCase.assertTrue(object.v==1);
		TestCase.assertTrue(object.sm.contains("AA"));
	}
	
	
	public void test13() {
		X1 object = new X1();
		Binding a = new Binding(object);
		IBinding z= (IBinding) a.getBinding("Sm");
		z.setValue("AA",null);
		TestCase.assertTrue(object.v==0);
		TestCase.assertTrue(object.sm.contains("AA"));
	}

}
