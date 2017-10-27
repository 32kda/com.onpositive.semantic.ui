package com.onpositive.semantic.model.expressions.operatorimplementations;

public class InstanceOfOperator extends BinaryOperator< Object, String > {

	public InstanceOfOperator(){
		super( BinaryOperator.INSTANCEOF, Object.class, String.class );
	}

	@Override
	protected Object doGetValue(Object obj, String className ) {
		
		try {
			if (className==null){
				return false;
			}
			Class<?> clazz = obj.getClass().getClassLoader().loadClass( className );
			return clazz.isInstance(obj) ; 
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	protected Object doGetValue(Object obj, Class<?> clazz){
		return clazz.isInstance(obj) ; 
	}
	
	public Object getValue( Object arg1 , Object arg2 )
	{
		if (arg2 instanceof Class)
			return doGetValue(arg1, (Class<?>) arg2);
		Object arg1casted = class1.cast(arg1) ;
		String arg2casted = class2.cast(arg2) ;
		
		return doGetValue( arg1casted, arg2casted ) ;
	}

//	public InstanceOfOperator( Class<Class<?>> class2 ) {
//		
//		super( BinaryOperator.INSTANCEOF, Object.class, Class.class );
//		//super( BinaryOperator.INSTANCEOF, Object.class, Class.class );
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	protected Object doGetValue(Object arg1, Class<?> arg2) {
//	
//		return arg1 instanceof arg2;
//	}

}
