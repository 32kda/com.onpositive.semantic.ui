package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.SortingRealm;
import com.onpositive.semantic.model.expressions.impl.ClosureExpression;

public class OrderByOperator extends BinaryOperator<Object, ClosureExpression> {

	private static final class ClosureComparator implements Comparator<Object>,IDescribableToQuery {
		private final ClosureExpression arg2;

		private ClosureComparator(ClosureExpression arg2) {
			this.arg2 = arg2;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public int compare(Object o1, Object o2) {
			Object compute = arg2.compute(o1);
			Object compute1 = arg2.compute(o2);
			if (compute==null){
				compute="";
			}
			if (compute1==null){
				compute1="";
			}
			try{
				Comparable l0=(Comparable) compute;
				Comparable l1=(Comparable) compute1;
				return l0.compareTo(l1);
			}catch (Exception e) {
				return compute.toString().compareTo(compute1.toString());
			}
		}

		@Override
		public boolean adapt(Query query) {
			ClosureExpression normalize = arg2.normalize();
			if (normalize != null) {
				if (normalize.writeSort(query)) {
					return true;
				}
			}
			return false;
		}
	}

	public OrderByOperator() {
		super(BinaryOperator.ORDER_BY, Object.class, ClosureExpression.class);
	}

	@Override
	protected Object doGetValue(Object arg1, final ClosureExpression arg2) {
		Comparator<Object> relation = new ClosureComparator(arg2);
		if (arg1 instanceof IRealm) {
			IRealm<?> r = (IRealm<?>) arg1;

			SortingRealm filteringRealm = new SortingRealm(r,
					relation);
			return filteringRealm;
		}
		Collection<Object> collection = ValueUtils.toCollection(arg1);
		ArrayList<Object> os = new ArrayList<Object>(collection);
		Collections.sort(os, relation);
		return os;
	}

}
