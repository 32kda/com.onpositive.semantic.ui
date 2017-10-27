package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.id.IIdentifiableObject;
import com.onpositive.semantic.model.api.id.IIdentifierProvider;
import com.onpositive.semantic.model.api.id.IdAccess;
import com.onpositive.semantic.model.api.id.SimpleRealmBasedIdentifierProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.java.BeanMetaProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Id;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.OrderedRealm;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public class BasicIdTest extends TestCase {

	@RealmProvider(ARealmProvider.class)
	static class A implements IIdentifiableObject {

		private String a;

		@Override
		public String getId() {
			return a;
		}

	}

	public static class ARealmProvider implements IRealmProvider<A> {

		@Override
		public IRealm<A> getRealm(IHasMeta model, Object parentObject,
				Object object) {
			ArrayList<A> s = new ArrayList<BasicIdTest.A>();
			for (int a = 0; a < 1000; a++) {
				A e = new A();
				e.a = "a" + a;
				s.add(e);
			}
			return new OrderedRealm<BasicIdTest.A>(s);
		}

	}

	@RealmProvider(BRealmProvider.class)
	static class B {
		@Id
		String b;
	}

	@RealmProvider(B1RealmProvider.class)
	static class B1 {
		@Id
		String b;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			B1 other = (B1) obj;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			return true;
		}
	}

	public static class BRealmProvider implements IRealmProvider<B> {

		@Override
		public IRealm<B> getRealm(IHasMeta model, Object parentObject,
				Object object) {
			ArrayList<B> s = new ArrayList<BasicIdTest.B>();
			for (int a = 0; a < 1000; a++) {
				B e = new B();
				e.b = "a" + a;
				s.add(e);
			}
			return new OrderedRealm<BasicIdTest.B>(s);
		}

	}

	public static class B1RealmProvider implements IRealmProvider<B1> {

		@Override
		public IRealm<B1> getRealm(IHasMeta model, Object parentObject,
				Object object) {
			ArrayList<B1> s = new ArrayList<BasicIdTest.B1>();
			for (int a = 0; a < 1000; a++) {
				B1 e = new B1();
				e.b = "a" + a;
				s.add(e);
			}
			return new OrderedRealm<BasicIdTest.B1>(s);
		}

	}

	public void test0() {
		A a = new A();
		a.a = "a12";
		Object id = IdAccess.getId(a);
		TestCase.assertEquals(id, a.a);
		A object = (A) IdAccess.getObject(A.class, id);
		TestCase.assertTrue(object.a.equals(a.a));

	}

	public void test1() {
		B a = new B();
		a.b = "a12";
		Object id = IdAccess.getId(a);
		TestCase.assertEquals(id, a.b);
		A object = (A) IdAccess.getObject(A.class, id);
		TestCase.assertTrue(object.a.equals(a.b));
	}

	public void test2() {
		B a = new B();
		a.b = "a12";
		TestCase.assertEquals(ExpressionAccess.calculate("@id", a), a.b);
	}

	public void test3() {
		B a = new B();
		a.b = "a12";
		// because object with this id is already known and is not equal to
		// given object
		TestCase.assertTrue(ValidationAccess.validate(a).isError());
	}

	public void test4() {
		B1 a = new B1();
		a.b = "a12";
		// because object with this id is already known but is equal to given
		// object
		TestCase.assertTrue(!ValidationAccess.validate(a).isError());
	}

	@RealmProvider(IdERealmProvider.class)
	static class IdE {
		@Id
		long id;
	}

	@SuppressWarnings("rawtypes")
	public static class IdERealmProvider implements IRealmProvider {

		@Override
		public IRealm getRealm(IHasMeta model, Object parentObject,
				Object object) {
			OrderedRealm<?> r = new OrderedRealm();
			r.registerService(IIdentifierProvider.class,
					new SimpleRealmBasedIdentifierProvider() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public IdE getObject(IHasMeta meta, Object parent,
								Object id) {
							if (id != null) {
								IdE result = new IdE();
								if (id instanceof Number) {
									result.id = ((Number) id).longValue();
									return result;
								}
								result.id = Long.parseLong(id.toString());
								return result;
							}
							return null;
						}

					});
			return r;
		}

	}

	public void test5() {
		
		String key = "class://com.onpositive.semantic.model.java.tests.BasicIdTests$IdE/3";
		Object global = GlobalAccess
				.getGlobal(GlobalAccess
						.stringToKey(key));
		TestCase.assertNotNull(global);
		IdE e = (IdE) global;
		TestCase.assertEquals(e.id, 3L);
		String keyString = GlobalAccess.keyString(e);
		System.out.println(keyString);
		TestCase.assertEquals(GlobalAccess.keyString(e), key);		
	}
	
	public static class B11{
		@Id
		String name="a";
	}
	
	public void test7(){
		Object id = IdAccess.getId(new B11());
		System.out.println(id);
	}
}
