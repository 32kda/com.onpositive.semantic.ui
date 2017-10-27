package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.onpositive.semantic.model.api.property.IBinaryOperator;

public abstract class BinaryOperator<T1,T2> implements IBinaryOperator{
	
	public static int operatorsCount = 28 ;
	public static int standartOperatorsCount = 22 ;
	
	//nonstandart operators:
	public static final int INSTANCEOF = 22 ;//">>>" ;
	public static final int CONTAINS   = 23 ;//">>>" ;
	public static final int FILTER_BY   = 24 ;//">>>" ;
	public static final int ORDER_BY   = 25 ;//">>>" ;
	public static final int TRANSFORM_BY   = 26 ;//">>>" ;
	public static final int COMMA = 27;	
	//standart operators:
//  public static final int       = 0 ; //"" ;
	public static final int MULT  = 3 ; //"*" ;
	public static final int DIV   = 4 ; //"/" ;
	
	public static final int PLUS  =  1 ; //"+" ;
	public static final int MINUS =  2 ; //"-" ;
	public static final int MOD   = 12 ; //"%" ;
	
	public static final int LSHIFT  = 18 ;//"<<" ;
	public static final int RSHIFT  = 19 ;//">>" ;
	public static final int TRSHIFT = 20 ;//">>>" ;
	
	public static final int AND = 13 ; //"&" ;
	public static final int OR  = 14 ; //"|" ;
	public static final int XOR = 17 ; //"^" ;
	
//  public static final int BACKSLASH = 5 ;// "\\" ;
	public static final int GREATER = 6 ;// ">" ;
	public static final int LOWER   = 7 ;//"<" ;
	public static final int GEQ =  8 ;//">=" ;
	public static final int LEQ =  9 ;//"<=" ;
	public static final int EQ  = 10 ;//"==" ;
	public static final int NEQ = 11 ;//"!=" ;
	
	
	public static final int L_AND = 15 ;//"&&" ;
	public static final int L_OR  = 16 ;//"||" ;
	
	public static final String INSTANCEOF_OPERATOR_STRING_LABEL = "instanceof" ;
	public static final String CONTAINS_OPERATOR_STRING_LABEL   = "contains" ;
	public static final String AND_LABEL = "and";
	public static final String OR_LABEL = "or";
	public static final String FILTER_BY_LABEL = "filterBy";
	public static final String ORDER_BY_LABEL = "orderBy";
	public static final String TRANSFORM_LABEL = "transformBy";

	
//	static final int POWER   = 23 ;//"" not implemented yet;
	
	static protected HashMap<String,Integer> map = new HashMap<String, Integer>() ;
	static public Integer getOperatorId( String s){
		return map.get(s) ;
	} 
	static{

		map.put( INSTANCEOF_OPERATOR_STRING_LABEL , INSTANCEOF ) ;
		map.put( CONTAINS_OPERATOR_STRING_LABEL   , CONTAINS   ) ;
		map.put( TRANSFORM_LABEL   , TRANSFORM_BY  ) ;
		map.put( ORDER_BY_LABEL   , ORDER_BY  ) ;
		map.put( FILTER_BY_LABEL   , FILTER_BY  ) ;
		
		//standart operators:
	//  public static final int = 0 ; //"" ;
		map.put( "*"  , MULT ) ;
		map.put( "/"  , DIV  ) ;
		
		map.put( "+"  , PLUS  ) ;
		map.put( "-"  , MINUS ) ;
		map.put( "%"  , MOD   ) ;
		
		map.put( "<<" , LSHIFT  ) ;
		map.put( ">>" , RSHIFT  ) ;
		map.put( ">>>", TRSHIFT ) ;
		
		map.put( "&"  , AND  ) ;
		map.put( "|"  , OR   ) ;
		map.put( "^"  , XOR  ) ;
		
		map.put( "&&"  , L_AND  ) ;
		map.put( "and"  , L_AND  ) ;
		map.put( "||"  , L_OR   ) ;
		map.put( "or"  , L_OR   ) ;
		
	//  public static final int BACKSLASH = 5 ;// "\\" ;
		map.put( ">"  , GREATER ) ;
		map.put( "<"  , LOWER) ;
		map.put( ">=" , GEQ  ) ;
		map.put( "<=" , LEQ  ) ;
		map.put( "," , COMMA  ) ;
		map.put( "==" , EQ   ) ;
		map.put( "!=" , NEQ  ) ;
	}		

	
	int kind ;
	final public Class<T1> class1 ;
	final public Class<T2> class2 ;
	
	static Object[] getCorrespondanceArray(){

		int typesCount = 5 ;
		boolean[][] compatibilityFlags = getCompatibilityFlags() ;
	
		Object[] result = new Object[ operatorsCount * typesCount * 3 ] ;
		for( int i = 0 ; i < result.length ; result[i++] = null ) ;
		
		int ind = 0 ;
		Class<?> classArray[] = { Integer.class, Long.class , Double.class , Boolean.class , String.class } ;
		for( int i = 0 ; i < standartOperatorsCount ; i++ )
			for( int j = 0 ; j < typesCount ; j++ )
			{
				if( compatibilityFlags[i][j] ){
					result[ ind++ ] = i ;
					result[ ind++ ] = new BinaryOperatorEvaluator.ClassPair( classArray[j], classArray[j] ) ;
					result[ ind++ ] = constructOperator(i,j) ;
				}							
			}	
		
		result[ ind++ ] = BinaryOperator.INSTANCEOF ;
		result[ ind++ ] = new BinaryOperatorEvaluator.ClassPair( Object.class, Class.class ) ;
		result[ ind++ ] = new InstanceOfOperator() ;
		
		result[ ind++ ] = BinaryOperator.CONTAINS ;
		result[ ind++ ] = new BinaryOperatorEvaluator.ClassPair( Collection.class, Object.class ) ;
		result[ ind++ ] = new ContainsOperator() ;
		
		result[ ind++ ] = BinaryOperator.CONTAINS ;
		result[ ind++ ] = new BinaryOperatorEvaluator.ClassPair( String.class, String.class ) ;
		result[ ind++ ] = new ContainsStringOperator() ;

		return result ;
	}
	
	static boolean[][] getCompatibilityFlags() {

		return  new boolean[][]{//  int |  long | double|   bool |  String
				new boolean[]  {  false , false , false ,  false ,  false  } ,// zero
				new boolean[]  {  true  , true  , true  ,  false ,  true   } ,// PLUS = "+" ;
				new boolean[]  {  true  , true  , true  ,  false ,  false  } ,// MINUS = "-" ;
				new boolean[]  {  true  , true  , true  ,  false ,  false  } ,// STAR = "*" ;
				new boolean[]  {  true  , true  , true  ,  false ,  false  } ,// SLASH = "/" ;
				new boolean[]  {  false , false , false ,  false ,  false  } ,// BACKSLASH = "\\" ;
				new boolean[]  {  true  , true  , true  ,  false ,  true   } ,// GREATER = ">" ;
				new boolean[]  {  true  , true  , true  ,  false ,  true   } ,// LOWER = "<" ;
				new boolean[]  {  true  , true  , true  ,  false ,  true   } ,// GEQ = ">=" ;
				new boolean[]  {  true  , true  , true  ,  false ,  true   } ,// LEQ = "<=" ;
				new boolean[]  {  true  , true  , true  ,  true  ,  true   } ,// EQ = "==" ;
				new boolean[]  {  true  , true  , true  ,  true  ,  true   } ,// NEQ = "!=" ;
				new boolean[]  {  true  , true  , true  ,  false ,  false  } ,// MOD = "%" ;
				new boolean[]  {  true  , true  , false ,  true  ,  false  } ,// AND = "&" ;
				new boolean[]  {  true  , true  , false ,  true  ,  false  } ,// OR = "|" ;
				new boolean[]  {  false , false , false ,  true  ,  false  } ,// L_AND = "&&" ;
				new boolean[]  {  false , false , false ,  true  ,  false  } ,// L_OR = "||" ;
				new boolean[]  {  true  , true  , false ,  true  ,  false  } ,// XOR = "^" ;
				new boolean[]  {  true  , true  , false ,  false ,  false  } ,// LSHIFT = "<<" ;
				new boolean[]  {  true  , true  , false ,  false ,  false  } ,// RSHIFT = ">>" ;
				new boolean[]  {  true  , true  , false ,  false ,  false  } ,// TRSHIFT = ">>>" ;
				new boolean[]  {  false , false , false ,  false ,  false  } ,// POWER = "" it is not implemented yet ;
		};
	}
	public static ArrayList<HashSet<Integer>> getPriorityLayers()
	{
		ArrayList<HashSet<Integer>> HSList = new ArrayList<HashSet<Integer>>() ;
		for( int i = 0 ; i < 11 ; i++ )
			HSList.add( new HashSet<Integer>() ) ;

	
	//	static final int L_OR  = 16 ;//"||" ;
		HSList.get(0).add( BinaryOperator.L_OR ) ;
	
	//	static final int L_AND = 15 ;//"&&" ;
		HSList.get(1).add( BinaryOperator.L_AND ) ;
	
	//	public static final int OR  = 14 ; //"|" ;		
		HSList.get(2).add( BinaryOperator.OR ) ;
	
	//	public static final int XOR = 17 ; //"^" ;
		HSList.get(3).add( BinaryOperator.XOR ) ;
	
	//	public static final int AND = 13 ; //"&" ;
		HSList.get(4).add( BinaryOperator.AND ) ;
	
	//	public static final int EQ  = 10 ;//"==" ;
	//	public static final int NEQ = 11 ;//"!=" ;
		HSList.get(5).add( BinaryOperator.EQ ) ;
		HSList.get(5).add( BinaryOperator.NEQ ) ;
	
	//	public static final int GREATER = 6 ;// ">" ;
	//	public static final int LOWER   = 7 ;//"<" ;
	//	public static final int GEQ =  8 ;//">=" ;
	//	public static final int LEQ =  9 ;//"<=" ;
	//	public static final int INSTANCEOF = 22 ;
	//	public static final int CONTAINS   = 23 ;
		HSList.get(6).add( BinaryOperator.GREATER ) ;
		HSList.get(6).add( BinaryOperator.LOWER ) ;
		HSList.get(6).add( BinaryOperator.GEQ ) ;
		HSList.get(6).add( BinaryOperator.LEQ ) ;
		HSList.get(6).add( BinaryOperator.INSTANCEOF ) ;
		HSList.get(6).add( BinaryOperator.CONTAINS ) ;
	
	//	public static final int LSHIFT  = 18 ;//"<<" ;
	//	public static final int RSHIFT  = 19 ;//">>" ;
	//	public static final int TRSHIFT = 20 ;//">>>" ;
		HSList.get(7).add( BinaryOperator.FILTER_BY ) ;
		HSList.get(7).add( BinaryOperator.ORDER_BY ) ;
		HSList.get(7).add( BinaryOperator.TRANSFORM_BY ) ;
		HSList.get(7).add( BinaryOperator.LSHIFT ) ;
		HSList.get(7).add( BinaryOperator.RSHIFT ) ;
		HSList.get(7).add( BinaryOperator.TRSHIFT ) ;
	
	//	public static final int PLUS  =  1 ; //"+" ;
	//	public static final int MINUS =  2 ; //"-" ;
		HSList.get(8).add( BinaryOperator.PLUS ) ;
		HSList.get(8).add( BinaryOperator.MINUS ) ;
		
	//	public static final int MULT  = 3 ; //"*" ;
	//	public static final int DIV   = 4 ; //"/" ;
	//	public static final int MOD   = 12 ; //"%" ;
		HSList.get(9).add( BinaryOperator.MULT ) ;
		HSList.get(9).add( BinaryOperator.DIV ) ;
		HSList.get(9).add( BinaryOperator.MOD ) ;
		HSList.get(10).add( BinaryOperator.COMMA ) ;
		return HSList ;
	}
	public static String[] getOperatorStringLabels(){
		
		Object[] tmp = map.keySet().toArray() ;
		String result[] = new String[ tmp.length ] ;
		for( int i = 0 ; i < tmp.length ; i++ )
			result[i] = (String)tmp[i] ;
		return result ;  
	}
	
	
	public static BinaryOperator<? extends Object, ? extends Object> constructOperator( int kind, int classIndex ){
		
		switch( classIndex ){
		
			case 0: return new IntegerOperator    ( kind ) ;
			case 1: return new LongIntegerOperator( kind ) ;
			case 2: return new DoubleOperator ( kind ) ;
			case 3: return new BooleanOperator( kind ) ;
			case 4: return new StringOperator ( kind ) ;
		}
		return null;
	}
	
	
	public BinaryOperator( int kind, Class<T1> class1, Class<T2> class2) {
		
		this.kind = kind ;		
		this.class1 = class1 ;
		this.class2 = class2 ;
	}
	

	public Object getValue( Object arg1 , Object arg2 )
	{
		T1 arg1casted = class1.cast(arg1) ;
		T2 arg2casted = class2.cast(arg2) ;
		
		return doGetValue( arg1casted, arg2casted ) ;
	}
	
	protected abstract Object doGetValue( T1 arg1, T2 arg2 );
}
