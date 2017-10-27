package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.Collection;

public class ContainsOperator extends BinaryOperator<Collection,Object >
{

public ContainsOperator() {
		super(BinaryOperator.CONTAINS , Collection.class , Object.class );
	}


	@Override
	protected Object doGetValue( Collection arg1, Object arg2) {
		
		if( ( arg1 == null )||( arg2 == null ) )
			return false ;
		
		return arg1.contains(arg2);
	}

}
