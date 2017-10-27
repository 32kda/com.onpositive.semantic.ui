package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.lang.reflect.Array;
import java.util.Collection;

public class BooleanOperator extends BinaryOperator<Object, Object>{

	public BooleanOperator(int kind){
		super(kind, Object.class, Object.class);

	}
	
	@Override
	public Object getValue( Object arg1 , Object arg2 )
	{
		return doGetValue( arg1, arg2 ) ;
	}

	@Override
	protected Object doGetValue(Object _arg1, Object _arg2) {
	// null and empty Collection are converted to false	

		boolean arg1, arg2 ;
		if( _arg1 == null )
		{
			arg1 = false ;
			if (kind==BinaryOperator.EQ){
				return _arg2==null;
			}
			if (kind==BinaryOperator.NEQ){
				return _arg2!=null;
			}
		}
		else{
			arg1 = ( ( _arg1 instanceof Boolean )	 ? Boolean.class.cast(_arg1) : true ) ;
			arg1 = ( ( _arg1 instanceof Collection ) ? !Collection.class.cast(_arg1).isEmpty() : arg1 ) ;			
			arg1 = ( ( _arg1 instanceof Array )      ? Array.getLength(_arg1) > 0 : arg1 ) ;
		}
		if( _arg2 == null )
		{			
			if (kind==BinaryOperator.EQ){
				return _arg1==null;
			}
			if (kind==BinaryOperator.NEQ){
				return _arg1!=null;
			}
			arg2 = false ;
		}
		else{
			arg2 = ( ( _arg2 instanceof Boolean )	 ? Boolean.class.cast(_arg2) :false ) ;
			arg2 = ( ( _arg2 instanceof Collection ) ? !Collection.class.cast(_arg2).isEmpty() : arg2 ) ;
			arg2 = ( ( _arg2 instanceof Array )      ? Array.getLength(_arg2) > 0 : arg2 ) ;
		}
		
		
		switch (kind){
		
//			case BinaryOperator.PLUS    :{ return arg1 +  arg2 ; }// 1  ; //"+" ;
//			case BinaryOperator.MINUS   :{ return arg1 -  arg2 ; }// 2  ; // "-" ;
//			case BinaryOperator.MULT    :{ return arg1 *  arg2 ; }// 3  ; // "*" ;
//			case BinaryOperator.DIV		:{ return arg1 /  arg2 ; }// 4  ; //"/" ;
//			case BinaryOperator.BACKSLASH:{return arg1    arg2 ; }// 5  ;// "\\" ;
//			case BinaryOperator.GREATER :{ return arg1 >  arg2 ; }// 6  ;// ">" ;
//			case BinaryOperator.LOWER   :{ return arg1 <  arg2 ; }// 7  ;//"<" ;
//			case BinaryOperator.GEQ 	:{ return arg1 >= arg2 ; }// 8  ;//">=" ;
//			case BinaryOperator.LEQ 	:{ return arg1 <= arg2 ; }// 9  ;//"<=" ;
			case BinaryOperator.EQ  	:{ return arg1 == arg2 ; }// 10 ;//"==" ;
			case BinaryOperator.NEQ 	:{ return arg1 != arg2 ; }// 11 ;//"!=" ;
//			case BinaryOperator.MOD 	:{ return arg1 %  arg2 ; }// 12 ;//"%" ;
			case BinaryOperator.AND 	:{ return arg1 &  arg2 ; }// 13 ;//"&" ;
			case BinaryOperator.OR  	:{ return arg1 |  arg2 ; }// 14 ;//"|" ;
			case BinaryOperator.L_AND	:{ return arg1 && arg2 ; }// 15 ;//"&&" ;
			case BinaryOperator.L_OR	:{ return arg1 || arg2 ; }// 16 ;//"||" ;
			case BinaryOperator.XOR     :{ return arg1 ^  arg2 ; }// 17 ;//"^" ;
//			case BinaryOperator.LSHIFT  :{ return arg1 << arg2 ; }// 18 ;//"<<" ;
//			case BinaryOperator.RSHIFT  :{ return arg1 >> arg2 ; }// 19 ;//">>" ;
//			case BinaryOperator.TRSHIFT :{ return arg1 >>>arg2 ; }// 20 ;//">>>" ;
		}
		return null;
	}
}
