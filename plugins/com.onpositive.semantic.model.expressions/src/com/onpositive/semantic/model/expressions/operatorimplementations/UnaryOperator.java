 package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.HashMap;

public abstract class UnaryOperator<T> {
	
	static int operatorsCount = 4 ;
	//standart operators:
//  static final int        = 0 ; //"" ;	
	public static final int UMINUS  = 102 ; // "-" ;
	public static final int L_NOT  = 103 ; // "!" ;
	public static final int BW_NOT = 104 ; // "~" ;
	public static final int NEW    = 105 ; // "new" ;
	
	public static final String NEW_OPERATOR_STRING_LABEL = "new" ;
	
	static protected HashMap<String,Integer> map = new HashMap<String, Integer>() ;
	static public int getOperatorId( String s){
		return map.get(s) ;
	}
	static{

		map.put( "-"  , UMINUS ) ;
		map.put( "!"  , L_NOT  ) ;
		map.put( "~"  , BW_NOT ) ;
		map.put( NEW_OPERATOR_STRING_LABEL, NEW    ) ;
	}	

	int kind ;
	final public Class<T> clazz ;
	
	
	static Object[] getCorrespondanceArray(){

		Object[] result = new Object[ operatorsCount * 3 ] ;
		for( int i = 0 ; i < result.length ; result[i++] = null ) ;
		
		int ind = 0 ;
		
		result[ ind++ ] = UnaryOperator.UMINUS ;
		result[ ind++ ] = Number.class ;
		result[ ind++ ] = new UnaryMinusOperator() ;
		
		result[ ind++ ] = UnaryOperator.L_NOT ;
		result[ ind++ ] = Boolean.class ;
		result[ ind++ ] = new BooleanNotOperator() ;
		
//		result[ ind++ ] = UnaryOperator.L_NOT ;
//		result[ ind++ ] = String.class ;
//		result[ ind++ ] = new StringNotOperator() ;
		
		result[ ind++ ] = UnaryOperator.BW_NOT ;
		result[ ind++ ] = Number.class ;
		result[ ind++ ] = new BitwiseNotOperator() ;
		
		result[ ind++ ] = UnaryOperator.NEW ;
		result[ ind++ ] = Class.class ;
		result[ ind++ ] = new NewOperator() ;

		return result ;
	}
	static Object[] getDefaultOperators(){

		Object[] result = new Object[ operatorsCount ] ;
		for( int i = 0 ; i < result.length ; result[i++] = null ) ;
		
		int ind = 0 ;
		
		result[ ind++ ] = new UnaryMinusOperator() ;
		result[ ind++ ] = new BooleanNotOperator() ;
		result[ ind++ ] = new BitwiseNotOperator() ;
		result[ ind++ ] = new NewOperator() ;

		return result ;
	}
	
	public static String[] getOperatorStringLabels(){
		
		Object[] tmp = map.keySet().toArray() ;
		String result[] = new String[ tmp.length ] ;
		for( int i = 0 ; i < tmp.length ; i++ )
			result[i] = (String)tmp[i] ;
		return result ;  
	}
	
	public UnaryOperator( int kind, Class<T> class1 ) {
		
		this.kind = kind ;		
		this.clazz = class1 ;
	}
	

	public Object getValue( Object arg )
	{
		try{
			T argCasted = clazz.cast(arg) ;
			return doGetValue( argCasted ) ;
		}
		catch( ClassCastException e ){
			return getDefaultValue() ;
		}
	}
	
	protected abstract Object doGetValue( T arg );
	protected abstract Object getDefaultValue() ;
}

