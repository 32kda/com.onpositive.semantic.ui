package com.onpositive.semantic.model.java.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.query.memimpl.InMemoryExecutor;

public class PerfTest {

	@Test
	public void test() {
		int[] m = new int[1000 * 10000];
		for (int a=0;a<m.length;a++){
			m[a]=a*2-3;
		}
		//****
		
		
		Point[] p=new Point[100*100000];
		for (int a=0;a<m.length;a++){
			p[a]=new Point();
			p[a].x=a*2-3;
		}
		List<Point> asList = Arrays.asList(p);
		for (int a=0;a<10;a++){
			long l0=System.currentTimeMillis();
			doFilt1(m);
			long l1=System.currentTimeMillis();
			System.out.println(l1-l0);
			doo(asList);
		}
	}


	protected void doo(List<Point> asList) {
		long l01=System.currentTimeMillis();
		InMemoryExecutor m=new InMemoryExecutor((List)asList);
		Query query = new Query(Point.class);
		query.addFilter(new QueryFilter("x", 5, QueryFilter.FILTER_LE));
		QueryResult execute = m.execute(query,null);
		//System.out.println(execute.getResult().length);
		long l11=System.currentTimeMillis();
		System.out.println(l11-l01);
	}
	

	protected void doFilt1(int[] m) {
		ArrayList<Integer>indexes=new ArrayList<Integer>();
		for (int a=0;a<m.length;a++){
			if (m[a]<5){
				indexes.add(a);
			}
		}
	}

}
