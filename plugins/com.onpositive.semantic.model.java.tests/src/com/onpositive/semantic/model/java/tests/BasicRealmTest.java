package com.onpositive.semantic.model.java.tests;

import java.awt.Point;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.GetPropertyExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.ListeningVariable;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.PropertyComparator;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.AndRealm;
import com.onpositive.semantic.model.api.realm.ExpressionToRealm;
import com.onpositive.semantic.model.api.realm.FilteringRealm;
import com.onpositive.semantic.model.api.realm.IDisposable;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.api.realm.OrRealm;
import com.onpositive.semantic.model.api.realm.OrderedRealm;
import com.onpositive.semantic.model.api.realm.PropertyFilter;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.realm.SortingRealm;
import com.onpositive.semantic.model.api.realm.TransformingRealm;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public class BasicRealmTest extends TestCase {

	public void test0() {
		OrderedRealm<String> r0 = new OrderedRealm<String>("a", "b");
		OrderedRealm<String> r1 = new OrderedRealm<String>("a", "b");
		OrRealm<String> r2 = new OrRealm<String>();
		r2.addRealm(r0);
		r2.addRealm(r1);
		TestCase.assertTrue(HashDelta.buildFrom(r2.getContents(),
				r0.getContents()).isEmpty());
	}

	public void test1() {
		OrderedRealm<String> r0 = new OrderedRealm<String>("a", "b");
		OrderedRealm<String> r1 = new OrderedRealm<String>("a", "b", "c");
		OrRealm<String> r2 = new OrRealm<String>();
		r2.addRealm(r0);
		r2.addRealm(r1);
		HashDelta<?> buildFrom = HashDelta.buildFrom(r0.getContents(),
				r2.getContents());
		TestCase.assertTrue(buildFrom.getAddedElements().size() == 1);
		TestCase.assertTrue(buildFrom.getAddedElements().contains("c"));
	}

	public void test2() {
		OrderedRealm<String> r0 = new OrderedRealm<String>("a", "b");
		OrderedRealm<String> r1 = new OrderedRealm<String>("a", "b", "c");
		AndRealm<String> r2 = new AndRealm<String>();
		r2.addRealm(r0);
		r2.addRealm(r1);
		TestCase.assertTrue(HashDelta.buildFrom(r2.getContents(),
				r0.getContents()).isEmpty());
	}

	public void test3() {
		OrderedRealm<String> r0 = new OrderedRealm<String>("a", "b");
		FilteringRealm<String> tz = new FilteringRealm<String>(r0,
				new PropertyFilter("this=='a'", null));
		TestCase.assertTrue(HashDelta
				.buildFrom(r0.getContents(), tz.getContents())
				.getRemovedElements().size() == 1);
		TestCase.assertTrue(tz.size() == 1);
		TestCase.assertTrue(tz.contains("a"));
	}

	int ts = 0;

	public void test4() {
		ts = 0;
		OrderedRealm<String> r0 = new OrderedRealm<String>("a", "b");
		ExpressionValueProperty expression = new ExpressionValueProperty(
				"true", null);
		FilteringRealm<String> tz = new FilteringRealm<String>(r0,
				new PropertyFilter(expression));
		TestCase.assertTrue(tz.size() == 2);
		TestCase.assertTrue(tz.contains("a"));
		tz.addRealmChangeListener(new IRealmChangeListener<String>() {

			@Override
			public void realmChanged(IRealm<String> realmn,
					ISetDelta<String> delta) {
				TestCase.assertTrue(delta.getRemovedElements().size() == 1);
				TestCase.assertTrue(delta.getRemovedElements().contains("b"));
				TestCase.assertTrue(delta.getChangedElements().isEmpty());
				TestCase.assertTrue(delta.getAddedElements().isEmpty());
				ts++;
			}
		});
		
		expression.setExpressionString("this=='a'");
		TestCase.assertTrue(tz.size() == 1);
		TestCase.assertTrue(tz.contains("a"));
		TestCase.assertTrue(!tz.contains("b"));
		tz.dispose();
		TestCase.assertEquals(ts, 1);
	}

	public void test5() {
		ArrayList<Point> pts = new ArrayList<Point>();
		for (int a = 0; a < 100; a++) {
			pts.add(new Point(a, a - 1));
		}
		OrderedRealm<?> ts = new OrderedRealm<Point>(pts);
		TransformingRealm tr = new TransformingRealm(ts,
				new ExpressionValueProperty("x", null));
		SortingRealm o = new SortingRealm(tr, new PropertyComparator(
				new ExpressionValueProperty("-this", null)));
		int a = 0;
		for (Object z : o.getContents()) {
			TestCase.assertEquals(z, 99 - a);
			a++;
		}
	}

	public void test6() {
		ArrayList<Point> ps = new ArrayList<Point>();
		OrderedRealm<Point> orderedRealm = new OrderedRealm<Point>(ps);
		Point object = new Point(1, 2);
		RealmAccess.addElement(orderedRealm, object);
		TestCase.assertTrue(orderedRealm.contains(object));
		RealmAccess.removeElement(orderedRealm, object);
		TestCase.assertFalse(orderedRealm.contains(object));
	}

	public void test7() {
		ArrayList<Point> ps = new ArrayList<Point>();
		IRealm<Point> orderedRealm = new OrderedRealm<Point>(ps);
		orderedRealm = new FilteringRealm<Point>(orderedRealm,
				new PropertyFilter(new ExpressionValueProperty("x+y<10", null)));
		Point object = new Point(1, 2);
		RealmAccess.addElement(orderedRealm, object);
		TestCase.assertTrue(orderedRealm.contains(object));
		RealmAccess.removeElement(orderedRealm, object);
		TestCase.assertFalse(orderedRealm.contains(object));
	}

	static class RealmHolder {
		ArrayList<Point> points = new ArrayList<Point>();
	}

	public void test8() {
		RealmHolder realmHolder = new RealmHolder();
		Point point = new Point(0, 1);
		realmHolder.points.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new GetPropertyExpression(
				"points", new ListeningVariable(realmHolder)));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		PropertyAccess.setValue("points", realmHolder, null);
		TestCase.assertTrue(value.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}

	public void test9() {
		Point point = new Point(0, 1);
		OrderedRealm<Point> ll = new OrderedRealm<Point>();
		ll.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new ListeningVariable(ll));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		RealmAccess.removeElement(ll, point);
		TestCase.assertTrue(value.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}

	public void test10() {
		Point point = new Point(0, 1);
		OrderedRealm<Point> ll = new OrderedRealm<Point>();
		ll.add(point);
		ListeningVariable ee = new ListeningVariable(ll);
		ExpressionToRealm m = new ExpressionToRealm(ee);
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		ee.setValue(new OrderedRealm<Object>());
		TestCase.assertTrue(value.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}

	public void test11() {
		Point point = new Point(0, 1);
		OrderedRealm<Point> ll = new OrderedRealm<Point>();
		ll.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new ListeningVariable(ll));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		RealmAccess.removeElement(value, point);
		TestCase.assertTrue(ll.size() == 0);
		TestCase.assertTrue(!ll.contains(point));
	}

	public void test12() {
		RealmHolder realmHolder = new RealmHolder();
		Point point = new Point(0, 1);
		realmHolder.points.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new GetPropertyExpression(
				"points", new ListeningVariable(realmHolder)));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));

		RealmAccess.removeElement(value, point);
		TestCase.assertTrue(realmHolder.points.size() == 0);
	}

	public void test13() {
		RealmHolder realmHolder = new RealmHolder();
		Point point = new Point(0, 1);
		realmHolder.points.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new GetPropertyExpression(
				"points", new ListeningVariable(realmHolder)));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		try {
			RealmAccess.addElement(value, this);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(value.size() == 1);
			TestCase.assertTrue(value.contains(point));
			return;
		}
		TestCase.assertTrue(false);
	}

	public void test14() {
		RealmHolder realmHolder = new RealmHolder();
		Point point = new Point(0, 1);
		// realmHolder.points.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new GetPropertyExpression(
				"points", new ListeningVariable(realmHolder)));
		IRealm<?> value = m.getValue();
		ICommand createRemoveFromRealm = RealmAccess.createRemoveFromRealm(
				value, point);
		TestCase.assertTrue(!ValidationAccess.validate(createRemoveFromRealm)
				.isError());
	}

	public void test15() {
		RealmHolder realmHolder = new RealmHolder();
		ExpressionToRealm m = new ExpressionToRealm(new GetPropertyExpression(
				"points", new ListeningVariable(realmHolder)));
		IRealm<?> value = m.getValue();
		ICommand createRemoveFromRealm = RealmAccess.createAddToRealmCommand(
				value, this);
		TestCase.assertTrue(ValidationAccess.validate(createRemoveFromRealm)
				.isError());

	}

	public void test16() {
		Point point = new Point(0, 1);
		ArrayList<Point> ll = new ArrayList<Point>();
		ll.add(point);
		ExpressionToRealm m = new ExpressionToRealm(new ListeningVariable(ll));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		RealmAccess.removeElement(value, point);
		TestCase.assertTrue(ll.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}

	public void test17() {
		Point point = new Point(0, 1);
		RealmHolder realmHolder = new RealmHolder();
		realmHolder.points.add(point);
		IExpressionEnvironment environment = new GetPropertyLookup(
				new ConstantExpression(realmHolder), null);
		ExpressionToRealm m = new ExpressionToRealm(ExpressionAccess.parse(
				"x->'points'", environment));
		TestCase.assertTrue(DefaultMetaKeys.isReadonly(m.getValue()));
		IRealm<?> value = m.getValue();
		TestCase.assertTrue(value.size() == 0);
		m = new ExpressionToRealm(ExpressionAccess.parse("this->'points'",
				environment));
		value = m.getValue();
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(m.getValue()));
		TestCase.assertTrue(value.size() == 1);
		TestCase.assertTrue(value.contains(point));
		RealmAccess.removeElement(value, point);
		TestCase.assertTrue(realmHolder.points.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void test18() {
		Point point = new Point(0, 1);
		RealmHolder realmHolder = new RealmHolder();
		realmHolder.points.add(point);
		IExpressionEnvironment environment = new GetPropertyLookup(
				new ConstantExpression(realmHolder), null);
		ExpressionToRealm m = new ExpressionToRealm(ExpressionAccess.parse(
				"this->'points'", environment));
		IRealm value = m.getValue();
		FilteringRealm<?> rs = new FilteringRealm<Object>(value,
				new PropertyFilter(new ExpressionValueProperty("x+y<0", null)));
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(m.getValue()));
		TestCase.assertTrue(rs.size() == 0);
		PropertyAccess.setValue("x", point, -10);
		TestCase.assertTrue(rs.contains(point));
		RealmAccess.removeElement(rs, point);
		TestCase.assertTrue(realmHolder.points.size() == 0);
		TestCase.assertTrue(!value.contains(point));
	}
	@SuppressWarnings({ })
	public void test19() {
		TestCase.assertEquals(ObjectChangeManager.realmSize(), 0);
		doTest();
		TestCase.assertEquals(ObjectChangeManager.realmSize(), 0);
	}
	
	
	static class Business{
		
		ArrayList<String>jobTypes=new ArrayList<String>();
		ArrayList<Job>jobs=new ArrayList<BasicRealmTest.Job>();
	}
	
	@RealmProvider(expression="business.jobs")
	static class Job{
	
		double salary;
		
		@RealmProvider(expression="$.business.jobTypes")
		String type;
		Business business;
	}
	
	public void test23(){
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business=business;
		Object calculate = ExpressionAccess.calculate("type.@realm", job);
		TestCase.assertTrue(calculate instanceof IRealm);
		IRealm<?>r=(IRealm<?>) calculate;
		TestCase.assertEquals(2,r.size());		
	}
	
	public void test20(){
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business=business;
		Object calculate = ExpressionAccess.calculate("type.@realm", job);
		TestCase.assertTrue(calculate instanceof IRealm);
		IRealm<?>r=(IRealm<?>) calculate;
		TestCase.assertEquals(2,r.size());
	}
	public void test21(){
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business=business;
		business.jobs.add(job);
		Object calculate = ExpressionAccess.calculate("@realm", job);
		TestCase.assertTrue(calculate instanceof IRealm);
		IRealm<?>r=(IRealm<?>) calculate;
		TestCase.assertEquals(1,r.size());
		TestCase.assertTrue(r.contains(job));
		TestCase.assertEquals(ObjectChangeManager.realmSize(), 0);		
	}
	
	public void test22(){
		Business business = new Business();
		business.jobTypes.add("Salor");
		business.jobTypes.add("Pirate");
		Job job = new Job();
		job.business=business;
		Object calculate = ExpressionAccess.calculate("@realm", job);
		TestCase.assertTrue(calculate instanceof IRealm);
		IRealm<?>r=(IRealm<?>) calculate;
		business.jobs.add(job);
		ObjectChangeManager.markChanged(business);
		TestCase.assertEquals(1,r.size());
		TestCase.assertTrue(r.contains(job));
		TestCase.assertEquals(ObjectChangeManager.realmSize(), 0);		
	}
	
	int count=0;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doTest() {
		count=0;
		Point point = new Point(0, 1);
		RealmHolder realmHolder = new RealmHolder();
		realmHolder.points.add(point);
		IExpressionEnvironment environment = new GetPropertyLookup(
				new ConstantExpression(realmHolder), null);
		ExpressionToRealm m = new ExpressionToRealm(ExpressionAccess.parse(
				"this->'points'", environment));
		IRealm value = m.getValue();
		FilteringRealm<?> rs = new FilteringRealm<Object>(value,
				new PropertyFilter(new ExpressionValueProperty("x+y<0", null)));		
		rs.addRealmChangeListener(new IRealmChangeListener() {

			@Override
			public void realmChanged(IRealm realmn,
					ISetDelta delta) {
				count++;				
			}
		});		
		TestCase.assertTrue(!DefaultMetaKeys.isReadonly(m.getValue()));
		TestCase.assertTrue(rs.size() == 0);
		PropertyAccess.setValue("x", point, -10);
		TestCase.assertTrue(rs.contains(point));
		RealmAccess.removeElement(rs, point);
		TestCase.assertTrue(realmHolder.points.size() == 0);
		m.dispose();
		TestCase.assertTrue(!value.contains(point));
		TestCase.assertEquals(count, 2);
		///System.gc();
	}
}
