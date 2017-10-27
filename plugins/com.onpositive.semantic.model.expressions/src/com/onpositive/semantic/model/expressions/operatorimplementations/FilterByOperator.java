package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.FilteringRealm;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.expressions.impl.ClosureExpression;

public class FilterByOperator extends BinaryOperator<Object, Object> {

	private final class ClosureFilter implements IFilter, IDescribableToQuery {
		private final ClosureExpression arg2;

		private ClosureFilter(ClosureExpression arg2) {
			this.arg2 = arg2;
		}

		@Override
		public void removeValueListener(IValueListener<?> listener) {
			arg2.removeValueListener(listener);
		}

		@Override
		public void addValueListener(IValueListener<?> listener) {
			arg2.addValueListener(listener);
		}

		@Override
		public boolean accept(Object element) {
			return ValueUtils.toBoolean(arg2.compute(element));
		}

		@Override
		public boolean adapt(Query query) {
			ClosureExpression s = arg2.normalize();
			if (s != null) {
				if (s.writeTo(query)) {
					return true;
				}
			}
			return false;
		}
	}

	public FilterByOperator() {
		super(BinaryOperator.FILTER_BY, Object.class, Object.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object doGetValue(Object arg1, Object value) {
		if (arg1 instanceof IRealm) {
			IRealm<?> r = (IRealm<?>) arg1;
			if (value instanceof ClosureExpression) {
				IFilter relation = new ClosureFilter((ClosureExpression) value);
				FilteringRealm<Object> filteringRealm = new FilteringRealm<Object>(
						(IRealm<Object>) r, relation);
				return filteringRealm;
			}
			boolean boolean1 = ValueUtils.toBoolean(value);
			if (boolean1) {
				return r;
			}
			return new FilteringRealm<Object>((IRealm<Object>) r,
					new FalseFilter());
		}

		Collection<Object> collection = ValueUtils.toCollection(arg1);
		if (value instanceof ClosureExpression) {
			ArrayList<Object> os = new ArrayList<Object>();
			for (Object m : collection) {
				if (ValueUtils
						.toBoolean(((ClosureExpression) value).compute(m))) {
					os.add(m);
				}
			}
			return os;
		}
		boolean boolean1 = ValueUtils.toBoolean(value);
		if (boolean1) {
			return collection;
		}
		return Collections.emptyList();
	}

}
