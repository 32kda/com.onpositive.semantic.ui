package com.onpositive.semantic.model.expressions.operatorimplementations;

public class UnaryMinusOperator extends UnaryOperator<Number> {

	public UnaryMinusOperator() {
		super( UnaryOperator.UMINUS , Number.class );
	}

	@Override
	protected Object doGetValue( Number arg) {
		if( arg == null )return null;
		
		if( arg.getClass() == Integer.class )
			return (-1)*(Integer)arg ;
		
		if( arg.getClass() ==  Long.class )
			return (-1)*(Long)arg ;
		
		if( arg.getClass() == Double.class )
			return (-1)*(Double)arg ;

		return null ;
	}

	@Override
	protected Object getDefaultValue() {
		return 0;
	}
	
	
}
