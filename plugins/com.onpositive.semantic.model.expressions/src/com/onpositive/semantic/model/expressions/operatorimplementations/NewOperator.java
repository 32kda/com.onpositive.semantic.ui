package com.onpositive.semantic.model.expressions.operatorimplementations;

public class NewOperator extends UnaryOperator<Class> {

	public NewOperator() {
		super( UnaryOperator.NEW , Class.class );
	}

	@Override
	protected Object doGetValue( Class clazz) {
		try {
			return clazz.newInstance() ;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null ;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null ;
		}
	}

	@Override
	protected Object getDefaultValue() {
		return null;
	}
	
	
}
