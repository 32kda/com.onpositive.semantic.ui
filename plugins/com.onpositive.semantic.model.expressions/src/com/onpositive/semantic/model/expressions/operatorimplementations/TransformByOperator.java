package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.TransformingRealm;
import com.onpositive.semantic.model.expressions.impl.ClosureExpression;

public class TransformByOperator extends
		BinaryOperator<Object, ClosureExpression> {

	public TransformByOperator() {
		super(BinaryOperator.TRANSFORM_BY, Object.class,
				ClosureExpression.class);
	}

	@SuppressWarnings({ "serial", "unused", "rawtypes" })
	@Override
	protected Object doGetValue(Object arg1, final ClosureExpression arg2) {
		if (arg1 instanceof IRealm<?>) {
			TransformingRealm rs = new TransformingRealm((IRealm) arg1,

			new ComputedProperty() {

				@Override
				public Object getValue(Object obj) {

					return arg2.compute(obj);
				}
			});
		}
		Collection<Object> collection = ValueUtils.toCollection(arg1);
		ArrayList<Object> l = new ArrayList<Object>();
		for (Object o : collection) {
			l.add(arg2.compute(o));
		}
		return l;
	}

}
