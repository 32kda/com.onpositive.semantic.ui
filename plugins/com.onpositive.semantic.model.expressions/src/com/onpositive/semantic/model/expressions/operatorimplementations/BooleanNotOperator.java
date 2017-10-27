package com.onpositive.semantic.model.expressions.operatorimplementations;

public class BooleanNotOperator extends UnaryOperator<Boolean> {

	public BooleanNotOperator() {
		super( UnaryOperator.L_NOT, Boolean.class );
	}

	@Override
	protected Object doGetValue(Boolean arg) {
		if ( arg == null ) return null;
		return !(Boolean)arg ;
	}

	@Override
	protected Object getDefaultValue() {
		return false;
	}
}
