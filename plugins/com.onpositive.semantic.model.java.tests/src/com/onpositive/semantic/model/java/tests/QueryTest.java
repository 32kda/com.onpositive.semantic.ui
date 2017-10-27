package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.query.memimpl.InMemoryExecutor;
import com.onpositive.semantic.model.api.query.memimpl.PartialInMemoryExecutor;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.api.realm.IResultUpdate;
import com.onpositive.semantic.model.api.realm.ParentedRealm;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.ui.core.Point;

public class QueryTest extends TestCase {

	static class PS {
		int age;
		String name;
	}

	public void test0() {
		Realm<Object> object = new Realm<Object>();
		object.registerService(IQueryExecutor.class, new IQueryExecutor() {

			public QueryResult execute(Query query) {
				QueryFilter queryFilter = query.getFilters()[0];
				return new QueryResult(queryFilter.getPropId(), queryFilter
						.getFilterConstraint(), queryFilter.getFilterKind());
			}

			@Override
			public QueryResult execute(Query query, IResultUpdate async) {
				return execute(query);
			}

			@Override
			public void cancel(IResultUpdate async) {

			}
		});
		ConstantExpression root = new ConstantExpression(object);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<10]", new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		Collection<?> contents = r.getContents();
		TestCase.assertTrue(contents.contains("age"));
		TestCase.assertTrue(contents.contains("<"));
		TestCase.assertTrue(contents.contains(10));
	}

	public void test1() {
		Realm<Object> object = new Realm<Object>();
		object.registerService(IQueryExecutor.class, new IQueryExecutor() {

			public QueryResult execute(Query query) {
				QueryFilter queryFilter = query.getFilters()[0];
				QueryFilter queryFilter1 = query.getFilters()[1];
				return new QueryResult(queryFilter.getPropId(), queryFilter
						.getFilterConstraint(), queryFilter.getFilterKind(),
						queryFilter1.getPropId(), queryFilter1
								.getFilterConstraint(), queryFilter1
								.getFilterKind());
			}

			@Override
			public QueryResult execute(Query query, IResultUpdate async) {
				return execute(query);
			}

			@Override
			public void cancel(IResultUpdate async) {
				// TODO Auto-generated method stub

			}
		});
		ConstantExpression root = new ConstantExpression(object);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<10 && name=='hello']",
				new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		Collection<?> contents = r.getContents();
		TestCase.assertTrue(contents.contains("age"));
		TestCase.assertTrue(contents.contains("<"));
		TestCase.assertTrue(contents.contains(10));
		TestCase.assertTrue(contents.contains("name"));
		TestCase.assertTrue(contents.contains("=="));
		TestCase.assertTrue(contents.contains("hello"));
	}

	public void test2() {
		Realm<Object> object = new Realm<Object>();
		object.registerService(IQueryExecutor.class, new IQueryExecutor() {

			public QueryResult execute(Query query) {
				QueryFilter queryFilter = query.getFilters()[0];
				QueryFilter queryFilter1 = query.getFilters()[1];
				return new QueryResult(queryFilter.getPropId(), queryFilter
						.getFilterConstraint(), queryFilter.getFilterKind(),
						queryFilter1.getPropId(), queryFilter1
								.getFilterConstraint(), queryFilter1
								.getFilterKind());
			}

			@Override
			public QueryResult execute(Query query, IResultUpdate async) {
				return execute(query);
			}

			@Override
			public void cancel(IResultUpdate async) {
				// TODO Auto-generated method stub

			}
		});
		ConstantExpression root = new ConstantExpression(object);
		GetPropertyLookup environment = new GetPropertyLookup(root, null);
		VariableExpression parentContext = new VariableExpression();
		parentContext.setValue(new Point(2, 10));
		environment.setParentContext(parentContext);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<$.$.vertical.value && name=='hello']",
				environment);
		IRealm<?> r = (IRealm<?>) parse.getValue();
		Collection<?> contents = r.getContents();
		TestCase.assertTrue(contents.contains("age"));
		TestCase.assertTrue(contents.contains("<"));
		TestCase.assertTrue(contents.contains(10.0));
	}

	public void test3() {
		Realm<Object> object = new Realm<Object>();
		object.registerService(IQueryExecutor.class, new IQueryExecutor() {

			public QueryResult execute(Query query) {
				QueryFilter queryFilter = query.getFilters()[0];
				QueryFilter queryFilter1 = query.getFilters()[1];
				return new QueryResult(queryFilter.getPropId(), queryFilter
						.getFilterConstraint(), queryFilter.getFilterKind(),
						queryFilter1.getPropId(), queryFilter1
								.getFilterConstraint(), queryFilter1
								.getFilterKind());
			}

			public QueryResult execute(Query query, IResultUpdate async) {
				return execute(query);
			}

			@Override
			public void cancel(IResultUpdate async) {
				// TODO Auto-generated method stub

			}
		});
		ConstantExpression root = new ConstantExpression(object);
		GetPropertyLookup environment = new GetPropertyLookup(root, null);
		VariableExpression parentContext = new VariableExpression();
		parentContext.setValue("age");
		environment.setParentContext(parentContext);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [(this->($.$))<10 && name=='hello']",
				environment);
		IRealm<?> r = (IRealm<?>) parse.getValue();
		Collection<?> contents = r.getContents();
		TestCase.assertTrue(contents.contains("age"));
		TestCase.assertTrue(contents.contains("<"));
		TestCase.assertTrue(contents.contains(10));
	}

	public void test4() {
		Realm<Object> object = new Realm<Object>();
		object.registerService(IQueryExecutor.class, new IQueryExecutor() {

			public QueryResult execute(Query query) {
				QueryFilter queryFilter = query.getFilters()[0];
				return new QueryResult(queryFilter.getPropId(), queryFilter
						.getFilterConstraint(), queryFilter.getFilterKind());
			}

			@Override
			public QueryResult execute(Query query, IResultUpdate async) {
				return execute(query);
			}

			@Override
			public void cancel(IResultUpdate async) {
				// TODO Auto-generated method stub

			}
		});
		ConstantExpression root = new ConstantExpression(object);
		GetPropertyLookup environment = new GetPropertyLookup(root, null);
		VariableExpression parentContext = new VariableExpression();
		parentContext.setValue(true);
		environment.setParentContext(parentContext);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy ($?[age<10]:true)", environment);
		IRealm<?> r = (IRealm<?>) parse.getValue();
		Collection<?> contents = r.getContents();
		TestCase.assertTrue(contents.contains("age"));
		TestCase.assertTrue(contents.contains("<"));
		TestCase.assertTrue(contents.contains(10));
	}

	static class Person {
		int age;
		String name;
		boolean isPublic;
	}

	public void test5() {
		System.out.println("Test5");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 40000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<5000] orderBy [isPublic]",
				new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		long l0 = System.currentTimeMillis();
		Collection<?> contents = r.getContents();
		long l1 = System.currentTimeMillis();
		TestCase.assertTrue(contents.size() == 5000);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		long l01 = System.currentTimeMillis();
		Collection<?> contents2 = r.getContents();
		long l02 = System.currentTimeMillis();
		TestCase.assertTrue(contents2.size() == 100);
		System.out.println(l1 - l0);
		System.out.println(l02 - l01);
	}

	public void test6() {
		System.out.println("Test6");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 40000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<5000 && isPublic==true]",
				new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		long l0 = System.currentTimeMillis();
		Collection<?> contents = r.getContents();
		long l1 = System.currentTimeMillis();
		TestCase.assertTrue(contents.size() == 2500);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		long l01 = System.currentTimeMillis();
		Collection<?> contents2 = r.getContents();
		long l02 = System.currentTimeMillis();
		TestCase.assertTrue(contents2.size() == 100);
		System.out.println(l1 - l0);
		System.out.println(l02 - l01);
	}

	public void test7() {
		System.gc();
		System.out.println("Test7");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 40000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<5000] ", new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		long l0 = System.currentTimeMillis();
		Collection<?> contents = r.getContents();
		long l1 = System.currentTimeMillis();
		TestCase.assertTrue(contents.size() == 5000);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		long l01 = System.currentTimeMillis();
		Collection<?> contents2 = r.getContents();
		long l02 = System.currentTimeMillis();
		TestCase.assertTrue(contents2.size() == 100);
		System.out.println(l1 - l0);
		System.out.println(l02 - l01);
	}

	public void test8() {
		System.out.println("Test8");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 40000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"this filterBy [age<5000] ", new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		BaseMeta m = (BaseMeta) r.getMeta();
		m.putMeta(DefaultMetaKeys.LIMIT, 20000);
		long l0 = System.currentTimeMillis();
		Collection<?> contents = r.getContents();
		long l1 = System.currentTimeMillis();
		TestCase.assertTrue(contents.size() == 5000);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		long l01 = System.currentTimeMillis();
		Collection<?> contents2 = r.getContents();
		long l02 = System.currentTimeMillis();
		TestCase.assertTrue(contents2.size() == 5000);
		System.out.println(l1 - l0);
		System.out.println(l02 - l01);
	}

	public void test9() {
		System.gc();
		System.out.println("Test9");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 40000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"(this filterBy [age<5000 || isPublic]) orderBy [age] ",
				new GetPropertyLookup(root, null));
		IRealm<?> r = (IRealm<?>) parse.getValue();
		BaseMeta m = (BaseMeta) r.getMeta();
		m.putMeta(DefaultMetaKeys.LIMIT, 20000);
		long l0 = System.currentTimeMillis();
		Collection<?> contents = r.getContents();
		long l1 = System.currentTimeMillis();
		TestCase.assertTrue(contents.size() == 22500);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		long l01 = System.currentTimeMillis();
		Collection<?> contents2 = r.getContents();
		long l02 = System.currentTimeMillis();
		TestCase.assertTrue(contents2.size() == 20000);
		System.out.println(l1 - l0);
		System.out.println(l02 - l01);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test10() {
		System.gc();
		System.out.println("Test10");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 1000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"(this filterBy [age<200 || isPublic]) orderBy [age] ",
				new GetPropertyLookup(root, null));
		ParentedRealm r = (ParentedRealm) parse.getValue();
		BaseMeta m = (BaseMeta) r.getMeta();
		int step = 20;
		m.putMeta(DefaultMetaKeys.LIMIT, step);
		obj.registerService(IQueryExecutor.class, new InMemoryExecutor(obj));
		int est = step;
		r.addRealmChangeListener(new IRealmChangeListener() {

			@Override
			public void realmChanged(IRealm realmn, ISetDelta delta) {
				// TODO Auto-generated method stub

			}
		});
		while (true) {
			Collection<?> contents2 = r.getContents();
			TestCase.assertTrue(contents2.size() == est);
			r.loadMoreData();
			if (r.getContents().size() == est) {
				TestCase.assertTrue(!r.isIncompleteDataHere());
				break;
			}
			est += 20;
		}
		Collection contents = r.getContents();
		ArrayList<Object> l = new ArrayList<Object>(contents);
		for (int a = 0; a < 200; a++) {
			Person p = (Person) l.get(a);
			TestCase.assertTrue(p.age == a);
		}
		for (int a = 200; a < 500; a++) {
			Person p = (Person) l.get(a);
			TestCase.assertTrue(p.age == 200 + (a - 200) * 2);
		}
		TestCase.assertTrue(contents.size() == 600);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test11() {
		System.gc();
		System.out.println("Test11");
		Realm<Object> obj = new Realm<Object>();
		for (int a = 0; a < 1000; a++) {
			Person element = new Person();
			element.age = a;
			element.name = "N" + a;
			element.isPublic = a % 2 == 0;
			obj.add(element);
		}
		ConstantExpression root = new ConstantExpression(obj);
		IListenableExpression<?> parse = ExpressionAccess.parse(
				"(this filterBy [age<200 && isPublic]) orderBy [age] ",
				new GetPropertyLookup(root, null));
		ParentedRealm r = (ParentedRealm) parse.getValue();
		BaseMeta m = (BaseMeta) r.getMeta();
		int step = 20;
		m.putMeta(DefaultMetaKeys.LIMIT, step);
		obj.registerService(IQueryExecutor.class, new PartialInMemoryExecutor(
				new InMemoryExecutor(obj)) {

			@Override
			protected LinkedHashSet<QueryFilter> getInStoreFilters(Query clone) {
				LinkedHashSet<QueryFilter>storeF=new LinkedHashSet<QueryFilter>();
				for (QueryFilter f:clone.getFilters()){
					if (!f.getFilterKind().equals(QueryFilter.FILTER_EQUALS)){
						storeF.add(f);
					}
				}
				return storeF;
			}
		});
		int est = step;
		r.addRealmChangeListener(new IRealmChangeListener() {

			@Override
			public void realmChanged(IRealm realmn, ISetDelta delta) {
				// TODO Auto-generated method stub

			}
		});
		while (true) {
			Collection<?> contents2 = r.getContents();					
			TestCase.assertTrue(contents2.size() == est);
			r.loadMoreData();
			if (r.getContents().size() == est) {
				TestCase.assertTrue(!r.isIncompleteDataHere());
				break;
			}
			est += 20;
		}
		List contents = (List) new ArrayList(r.getContents());
		for (int a=0;a<contents.size();a++){
			Person p=(Person) contents.get(a);
			TestCase.assertTrue(p.age==2*a);
			TestCase.assertTrue(p.isPublic);
		}
		TestCase.assertTrue(contents.size() == 100);
	}
	
	static class TP{
		
	}
	public void test12(){
		Query q=new Query(TP.class);
		q.preprocess();
		TestCase.assertTrue(q.getFilters().length>0);
	}
}
