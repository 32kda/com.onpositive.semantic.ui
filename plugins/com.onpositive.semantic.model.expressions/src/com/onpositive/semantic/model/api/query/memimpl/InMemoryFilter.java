package com.onpositive.semantic.model.api.query.memimpl;

import java.lang.reflect.Array;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperatorEvaluator;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperatorEvaluator.ClassPair;

public class InMemoryFilter extends AbstractFilter implements IFilter {

	private static final long serialVersionUID = 5301009977272597021L;

	static class InMemQuery {

		private final String prop;

		public InMemQuery(QueryFilter q) {
			super();
			this.constraint = q.getFilterConstraint();
			String filterKind2 = q.getFilterKind();
			kind = calcKind(filterKind2);
			this.prop = q.getPropId();
		}

		private int calcKind(String filterKind2) {
			if (filterKind2.equals(QueryFilter.FILTER_GE)) {
				return BinaryOperator.GEQ;
			}
			if (filterKind2.equals(QueryFilter.FILTER_LE)) {
				return BinaryOperator.LEQ;
			}
			if (filterKind2.equals(QueryFilter.FILTER_GT)) {
				return BinaryOperator.GREATER;
			}
			if (filterKind2.equals(QueryFilter.FILTER_LT)) {
				return BinaryOperator.LOWER;
			}
			if (filterKind2.equals(QueryFilter.FILTER_CONTAINS)) {
				return BinaryOperator.CONTAINS;
			}
			if (filterKind2.equals(QueryFilter.FILTER_NOT_EQUALS)) {
				return BinaryOperator.NEQ;
			}
			if (filterKind2.equals(QueryFilter.FILTER_EQUALS)) {
				return BinaryOperator.EQ;
			}
			if (filterKind2.equals(QueryFilter.FILTER_STARTS_WITH)) {
				return -1;
			}
			if (filterKind2.equals(QueryFilter.FILTER_ALL_OF)) {
				return -2;
			}
			if (filterKind2.equals(QueryFilter.FILTER_ONE_OF)) {
				return -3;
			}
			return -10;
		}

		protected IProperty pr;
		protected BinaryOperator<Object, Object> op;

		protected final Object constraint;
		protected final int kind;
		protected Class<?> lc;
		private boolean multivalue;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public final boolean accept(Object object) {
			Class<? extends Object> class1 = object.getClass();
			if (lc != class1) {
				pr = null;
				op = null;
				lc = class1;
			}
			if (pr == null) {
				pr = PropertyAccess.getProperty(object, prop);
				if (pr == null) {
					return false;
				}
			}
			if (op == null) {
				Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(pr);
				multivalue = DefaultMetaKeys.isMultivalue(pr);
				Class<?> class10 = subjectClass;
				Class<?> class20 = constraint == null ? class10 : constraint
						.getClass();
				BinaryOperator<?, ?> operator = BinaryOperatorEvaluator
						.getOperator(kind, new ClassPair(class10, class20));
				this.op = (BinaryOperator<Object, Object>) operator;
			}
			int kind2 = kind;
			if (kind2 > 0) {
				Object value = pr.getValue(object);
				if (multivalue&&kind2!=BinaryOperator.CONTAINS) {
					if (value instanceof Iterable) {
						Iterable c = (Iterable) value;
						for (Object o : c) {
							Object value2 = op.getValue(o, constraint);
							boolean boolean1 = ValueUtils.toBoolean(value2);
							if (boolean1) {
								return true;
							}
							
						}
						return false;
					}
					if (value instanceof Object[]) {
						Object[] c = (Object[]) value;
						for (Object o : c) {
							Object value2 = op.getValue(o, constraint);
							boolean boolean1 = ValueUtils.toBoolean(value2);
							if (boolean1) {
								return true;
							}
						}
						return false;
					}
					if (value != null && value.getClass().isArray()) {
						int length = Array.getLength(value);
						for (int a = 0; a < length; a++) {
							Object object2 = Array.get(value, a);
							Object value2 = op.getValue(object2, constraint);
							boolean boolean1 = ValueUtils.toBoolean(value2);
							if (boolean1) {
								return true;
							}

						}
						return false;
					}
				}
				Object value2 = op.getValue(value, constraint);
				return ValueUtils.toBoolean(value2);
			} else {
				throw new UnsupportedOperationException();
			}
		}
	}

	protected InMemQuery[]queries;

	public  InMemoryFilter(QueryFilter[]f) {
		queries=new InMemQuery[f.length];
		int a=0;
		for (QueryFilter q:f){
			queries[a++]=new InMemQuery(q);
		}
	}

	@Override
	public boolean accept(Object element) {
		for (InMemQuery c: queries){
			if (!c.accept(element)){
				return false;
			}
		}
		return true;
	}
}
