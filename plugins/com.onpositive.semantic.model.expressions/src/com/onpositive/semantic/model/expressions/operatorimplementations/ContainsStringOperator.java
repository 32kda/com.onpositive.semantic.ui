package com.onpositive.semantic.model.expressions.operatorimplementations;


public class ContainsStringOperator extends BinaryOperator<String, String>{

	public ContainsStringOperator() {
		super(BinaryOperator.CONTAINS , String.class ,String.class); 
	}

	@Override
	protected Object doGetValue(String arg1, String arg2) {
		if (arg1==null){
			return arg2==null;
		}		
		if (arg2==null){
			return arg1==null;
		}
		return arg1.indexOf(arg2)!=-1;
	}

}
