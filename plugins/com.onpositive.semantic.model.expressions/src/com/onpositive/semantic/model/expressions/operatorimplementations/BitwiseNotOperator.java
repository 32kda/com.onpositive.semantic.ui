package com.onpositive.semantic.model.expressions.operatorimplementations;

public class BitwiseNotOperator extends UnaryOperator<Number> {

	public BitwiseNotOperator() {
		super( UnaryOperator.BW_NOT, Number.class );
	}

	@Override
	protected Object doGetValue(Number arg) {
		if( arg == null )return null;
		
		if( arg.getClass() == Integer.class )
			return ~(Integer)arg ;
		
		if( arg.getClass() ==  Long.class )
			return ~(Long)arg ;
		
		return null ;
	}

	@Override
	protected Object getDefaultValue() {
		return 0 ;
	}

}
