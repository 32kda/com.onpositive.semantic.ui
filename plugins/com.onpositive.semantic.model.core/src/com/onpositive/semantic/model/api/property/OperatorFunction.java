package com.onpositive.semantic.model.api.property;

public class OperatorFunction extends Function<Object, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Object vl0;
	protected final boolean inverse;
	protected final IBinaryOperator operator;

	public OperatorFunction(Object vl0, boolean inverse,
			IBinaryOperator operator) {
		super();
		this.vl0 = vl0;
		this.inverse = inverse;
		this.operator = operator;
	}

	@Override
	public Object apply(Object obj) {
		if (inverse) {
			return operator.getValue(obj, vl0);
		}
		return operator.getValue(vl0, obj);
	}
}
