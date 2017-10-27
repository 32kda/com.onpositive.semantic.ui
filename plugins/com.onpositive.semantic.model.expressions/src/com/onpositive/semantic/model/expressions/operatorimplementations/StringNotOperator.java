package com.onpositive.semantic.model.expressions.operatorimplementations;

public class StringNotOperator extends UnaryOperator<String> {

	public StringNotOperator() {
		super( UnaryOperator.L_NOT, String.class );
	}

	@Override
	protected Object doGetValue(String arg) {
		return arg==null||arg.length()==0 ;
	}

	@Override
	protected Object getDefaultValue() {
		return false;
	}
}